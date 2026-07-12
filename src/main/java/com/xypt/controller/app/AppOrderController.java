package com.xypt.controller.app;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xypt.common.Result;
import com.xypt.entity.*;
import com.xypt.service.*;
import com.xypt.utils.RedisLockUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
/** 订单核心业务Controller（学生发单/跑腿员接单） */
@RestController
@RequestMapping("/api/app/orders")
public class AppOrderController {
    @Autowired OrdersService ordersService;
    @Autowired UserService userService;
    @Autowired EvaluationService evaluationService;
    @Autowired OrderRefundService refundService;
    @Autowired OrderComplaintService complaintService;
    @Autowired MessageService messageService;
    @Autowired ReputationRecordService reputationRecordService;
    @Autowired BalanceRecordService balanceRecordService;
    @Autowired RedisLockUtil redisLockUtil;  // Redis 分布式锁

    /** 学生发布订单 POST /api/app/orders */
    @PostMapping
    @Transactional
    public Result<String> createOrder(@RequestBody Orders order, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        User student = userService.getById(userId);
        if (student.getReputation()<60) return Result.error("信誉分不足60，无法发单");
        order.setStudentId(userId);
        order.setStatus(0);
        order.setFaceVerified(0);
        order.setOrderNo("ORD"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))+String.format("%03d",new Random().nextInt(1000)));
        order.setCreateTime(LocalDateTime.now());
        ordersService.save(order);
        // 发送消息给跑腿员
        sendSystemMessage(userId,"订单已发布","您的订单"+order.getOrderNo()+"已发布，等待跑腿员接单","order");
        return Result.success("发单成功");
    }

    /** 学生获取自己的订单列表 GET /api/app/orders/my */
    @GetMapping("/my")
    public Result<List<Map<String,Object>>> myOrders(@RequestParam(required=false) Integer status, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        LambdaQueryWrapper<Orders> w = new LambdaQueryWrapper<Orders>().eq(Orders::getStudentId,userId);
        if (status!=null) w.eq(Orders::getStatus,status);
        w.orderByDesc(Orders::getCreateTime);
        List<Orders> orders = ordersService.list(w);
        return Result.success(buildOrderList(orders));
    }

    /** 获取可接单列表（跑腿员用）GET /api/app/orders/available */
    @GetMapping("/available")
    public Result<List<Map<String,Object>>> available(@RequestParam(required=false) String serviceType, HttpSession session) {
        if (session.getAttribute("userId")==null) return Result.unauthorized();
        LambdaQueryWrapper<Orders> w = new LambdaQueryWrapper<Orders>().eq(Orders::getStatus,0);
        if (serviceType!=null&&!serviceType.isEmpty()) w.eq(Orders::getServiceType,serviceType);
        w.orderByDesc(Orders::getReward).orderByDesc(Orders::getCreateTime);
        return Result.success(buildOrderList(ordersService.list(w)));
    }

    /**
     * 跑腿员抗单/接单  PUT /api/app/orders/{id}/grab
     *
     * 防并发方案：双重保护
     *   1》Redis 分布式锁（第一道）：同一订单同时只有一个请求能进入数据库操作
     *   2》数据库条件更新（第二道）：UPDATE orders ... WHERE id=? AND status=0，天然原子性兑巫
     */
    @PutMapping("/{id}/grab")
    public Result<String> grabOrder(@PathVariable Long id, HttpSession session) {
        Long courierId = (Long) session.getAttribute("userId");
        if (courierId == null) return Result.unauthorized();

        // 前置校验：跑腿员资格
        User courier = userService.getById(courierId);
        if (courier == null) return Result.error("用户不存在");
        if (courier.getCourierStatus() != 2) return Result.error("您尚未成为认证跑腿员");
        if (courier.getReputation() < 60) return Result.error("信誉分不足60分，无法接单");

        // ===== 第一道防护：Redis 分布式锁 =====
        // key: order:grab:{orderId}，value: courierId，过期10秒
        String lockKey = "order:grab:" + id;
        String lockValue = String.valueOf(courierId);
        boolean locked = redisLockUtil.tryLock(lockKey, lockValue, 10);
        if (!locked) {
            // 锁已被占用，说明正有其他跑腿员在操作该订单
            return Result.error("手速不够快，订单已被其他跑腿员抢走！");
        }

        try {
            // 快速检查订单是否存在且处于待接单状态
            Orders order = ordersService.getById(id);
            if (order == null) return Result.error("订单不存在");
            if (order.getStatus() != 0) return Result.error("该订单已被接单");

            // ===== 第二道防护：数据库条件更新（乐观锁思想）=====
            // WHERE id=? AND status=0 天然具备原子性，即使分布式锁失效也能兑巫
            boolean updated = ordersService.update(
                new LambdaUpdateWrapper<Orders>()
                    .eq(Orders::getId, id)
                    .eq(Orders::getStatus, 0)            // 只有 status=0 才能被抢单
                    .set(Orders::getCourierId, courierId)
                    .set(Orders::getStatus, 1)
                    .set(Orders::getGrabTime, LocalDateTime.now())
            );

            if (!updated) {
                // 数据库层面抢单失败（极香情况：分布式锁未能完全阻止并发，DB 兄巫）
                return Result.error("抢单失败，订单已被其他跑腿员接走");
            }

            // 抢单成功，发送通知给学生
            sendSystemMessage(order.getStudentId(), "订单已接单",
                "跑腿员" + courier.getRealName() + "已接单，正在前往取件", "order");
            return Result.success("接单成功");

        } finally {
            // 必须在 finally 里释放锁，防止异常时锁永不释放
            // 安全释放：只释放自己持有的锁
            redisLockUtil.releaseLock(lockKey, lockValue);
        }
    }

    /** 跑腿员更新订单状态 PUT /api/app/orders/{id}/status
     *  允许的状态转换：1(待取件)→2(配送中) 或 2(配送中)→3(已送达) */
    @PutMapping("/{id}/status")
    @Transactional
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody Map<String,Integer> body, HttpSession session) {
        Long courierId = (Long) session.getAttribute("userId");
        if (courierId == null) return Result.unauthorized();
        Orders order = ordersService.getById(id);
        if (order == null || !courierId.equals(order.getCourierId())) return Result.error("无权操作");
        Integer newStatus = body.get("status");
        if (newStatus == null) return Result.error("请传入状态值");
        // 校验状态转换是否合法：只允许逐步推进 1→2→3
        int current = order.getStatus();
        if (!((current == 1 && newStatus == 2) || (current == 2 && newStatus == 3))) {
            return Result.error("状态转换不合法，只允许待取件→配送中或配送中→已送达");
        }
        order.setStatus(newStatus);
        if (newStatus == 3) {
            sendSystemMessage(order.getStudentId(), "快递已送达",
                "您的订单" + order.getOrderNo() + "已送达，请完成人脸验证后确认收货", "order");
        }
        ordersService.updateById(order);
        return Result.success("状态更新成功");
    }

    /** 学生确认收货 PUT /api/app/orders/{id}/confirm
     *  需先通过人脸验证（faceVerified=1），防止冒领 */
    @PutMapping("/{id}/confirm")
    @Transactional
    public Result<String> confirmReceive(@PathVariable Long id, HttpSession session) {
        Long studentId = (Long) session.getAttribute("userId");
        if (studentId==null) return Result.unauthorized();
        Orders order = ordersService.getById(id);
        if (order==null||!studentId.equals(order.getStudentId())) return Result.error("无权操作");
        if (order.getStatus()!=3) return Result.error("订单状态不对，无法确认收货");
        // 人脸验证校验：必须先通过人脸验证才能确认收货
        if (order.getFaceVerified()==null || order.getFaceVerified()!=1) {
            return Result.error("请先完成人脸验证后再确认收货");
        }
        order.setStatus(4); order.setCompleteTime(LocalDateTime.now());
        ordersService.updateById(order);
        // 赏金转入跑腿员余额
        if (order.getCourierId()!=null && order.getReward()!=null) {
            User courier = userService.getById(order.getCourierId());
            courier.setBalance(courier.getBalance().add(order.getReward()));
            courier.setTotalEarnings(courier.getTotalEarnings().add(order.getReward()));
            userService.updateById(courier);
            BalanceRecord br = new BalanceRecord(); br.setUserId(order.getCourierId()); br.setAmount(order.getReward());
            br.setType(1); br.setRemark("订单"+order.getOrderNo()+"完成收益"); br.setOrderId(id); br.setCreateTime(LocalDateTime.now());
            balanceRecordService.save(br);
            sendSystemMessage(order.getCourierId(),"订单完成收益","订单"+order.getOrderNo()+"已完成，赏金"+order.getReward()+"元已入账","system");
        }
        // 加信誉分
        addReputation(studentId, 2, "完成订单"+order.getOrderNo());
        if (order.getCourierId()!=null) addReputation(order.getCourierId(), 2, "完成订单"+order.getOrderNo());
        return Result.success("确认收货成功，请为跑腿员评价");
    }

    /** 学生取消订单 PUT /api/app/orders/{id}/cancel */
    @PutMapping("/{id}/cancel")
    public Result<String> cancelOrder(@PathVariable Long id, @RequestBody Map<String,String> body, HttpSession session) {
        Long studentId = (Long) session.getAttribute("userId");
        if (studentId==null) return Result.unauthorized();
        Orders order = ordersService.getById(id);
        if (order==null||!studentId.equals(order.getStudentId())) return Result.error("无权操作");
        if (order.getStatus()>1) return Result.error("订单进行中，无法取消");
        order.setStatus(5); order.setCancelReason(body.getOrDefault("reason","用户主动取消"));
        ordersService.updateById(order);
        return Result.success("取消成功");
    }

    /** 学生评价跑腿员 POST /api/app/orders/{id}/evaluate */
    @PostMapping("/{id}/evaluate")
    @Transactional
    public Result<String> evaluate(@PathVariable Long id, @RequestBody Evaluation evaluation, HttpSession session) {
        Long studentId = (Long) session.getAttribute("userId");
        if (studentId==null) return Result.unauthorized();
        Orders order = ordersService.getById(id);
        if (order==null||!studentId.equals(order.getStudentId())) return Result.error("无权操作");
        if (order.getStatus()!=4) return Result.error("只有已完成的订单才能评价");
        long count = evaluationService.count(new LambdaQueryWrapper<Evaluation>().eq(Evaluation::getOrderId,id));
        if (count>0) return Result.error("该订单已评价过");
        evaluation.setOrderId(id); evaluation.setStudentId(studentId); evaluation.setCourierId(order.getCourierId());
        evaluation.setCreateTime(LocalDateTime.now());
        evaluationService.save(evaluation);
        // 根据评分加减信誉分
        if (evaluation.getRating()>=4) addReputation(order.getCourierId(),1,"订单"+order.getOrderNo()+"获得好评");
        else if (evaluation.getRating()<=2) addReputation(order.getCourierId(),-3,"订单"+order.getOrderNo()+"获得差评");
        return Result.success("评价成功");
    }

    /** 学生申请退款 POST /api/app/orders/{id}/refund */
    @PostMapping("/{id}/refund")
    public Result<String> applyRefund(@PathVariable Long id, @RequestBody Map<String,String> body, HttpSession session) {
        Long studentId = (Long) session.getAttribute("userId");
        if (studentId==null) return Result.unauthorized();
        Orders order = ordersService.getById(id);
        if (order==null||!studentId.equals(order.getStudentId())) return Result.error("无权操作");
        OrderRefund refund = new OrderRefund();
        refund.setOrderId(id); refund.setStudentId(studentId); refund.setReason(body.get("reason"));
        refund.setStatus(0); refund.setCreateTime(LocalDateTime.now());
        refundService.save(refund);
        return Result.success("退款申请已提交");
    }

    /** 学生提交投诉 POST /api/app/orders/{id}/complaint */
    @PostMapping("/{id}/complaint")
    public Result<String> submitComplaint(@PathVariable Long id, @RequestBody OrderComplaint complaint, HttpSession session) {
        Long studentId = (Long) session.getAttribute("userId");
        if (studentId==null) return Result.unauthorized();
        complaint.setOrderId(id); complaint.setStudentId(studentId); complaint.setStatus(0);
        complaint.setCreateTime(LocalDateTime.now());
        complaintService.save(complaint);
        return Result.success("投诉已提交");
    }

    /** 获取订单详情 GET /api/app/orders/{id} */
    @GetMapping("/{id}")
    public Result<Map<String,Object>> detail(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("userId")==null) return Result.unauthorized();
        Orders o = ordersService.getById(id);
        if (o==null) return Result.error("订单不存在");
        Map<String,Object> m = new HashMap<>();
        m.put("id",o.getId()); m.put("orderNo",o.getOrderNo()); m.put("serviceType",o.getServiceType());
        m.put("pickupAddress",o.getPickupAddress()); m.put("deliveryAddress",o.getDeliveryAddress());
        m.put("itemInfo",o.getItemInfo()); m.put("reward",o.getReward()); m.put("note",o.getNote());
        m.put("status",o.getStatus()); m.put("createTime",o.getCreateTime());
        m.put("grabTime",o.getGrabTime()); m.put("completeTime",o.getCompleteTime());
        // 地图经纬度（供小程序地图组件展示标记）
        m.put("pickupLat",o.getPickupLat()); m.put("pickupLng",o.getPickupLng());
        m.put("deliveryLat",o.getDeliveryLat()); m.put("deliveryLng",o.getDeliveryLng());
        m.put("distance",o.getDistance());
        m.put("faceVerified", o.getFaceVerified()!=null && o.getFaceVerified()==1);
        // 学生信息（必须包含 studentId供跑腿员聊天页使用）
        m.put("studentId", o.getStudentId());
        User student = userService.getById(o.getStudentId());
        m.put("studentName", student!=null?student.getRealName():""); m.put("studentPhone", student!=null?student.getPhone():"");
        m.put("studentAvatar", student!=null?student.getAvatar():"");
        // 跑腿员信息（必须包含 courierId供学生聊天页使用）
        m.put("courierId", o.getCourierId());
        if (o.getCourierId()!=null) {
            User courier = userService.getById(o.getCourierId());
            m.put("courierName", courier!=null?courier.getRealName():""); m.put("courierPhone", courier!=null?courier.getPhone():"");
            m.put("courierAvatar", courier!=null?courier.getAvatar():""); m.put("courierReputation", courier!=null?courier.getReputation():100);
        }
        // 是否已评价（供前端隐藏/灰化评价按钮）
        long evalCount = evaluationService.count(new LambdaQueryWrapper<Evaluation>().eq(Evaluation::getOrderId, id));
        m.put("hasEvaluated", evalCount > 0);
        // 学生是否已注册人脸（供前端提示）
        m.put("studentHasFace", student!=null && student.getFacePhoto()!=null && !student.getFacePhoto().isEmpty());
        return Result.success(m);
    }

    /** 获取跑腿员实时位置 GET /api/app/orders/{id}/courier-location */
    @GetMapping("/{id}/courier-location")
    public Result<Map<String,Object>> courierLocation(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("userId")==null) return Result.unauthorized();
        Orders order = ordersService.getById(id);
        if (order==null||order.getCourierId()==null) return Result.error("订单无跑腿员");
        User courier = userService.getById(order.getCourierId());
        Map<String,Object> m = new HashMap<>();
        m.put("lat", courier!=null?courier.getCurrentLat():null);
        m.put("lng", courier!=null?courier.getCurrentLng():null);
        m.put("updateTime", courier!=null?courier.getLocationUpdateTime():null);
        return Result.success(m);
    }

    private List<Map<String,Object>> buildOrderList(List<Orders> orders) {
        List<Map<String,Object>> result = new ArrayList<>();
        orders.forEach(o -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id",o.getId()); m.put("orderNo",o.getOrderNo()); m.put("serviceType",o.getServiceType());
            m.put("pickupAddress",o.getPickupAddress()); m.put("deliveryAddress",o.getDeliveryAddress());
            m.put("itemInfo",o.getItemInfo()); m.put("reward",o.getReward()); m.put("status",o.getStatus());
            m.put("createTime",o.getCreateTime());
            if (o.getCourierId()!=null) { User c = userService.getById(o.getCourierId()); m.put("courierName",c!=null?c.getRealName():""); m.put("courierAvatar",c!=null?c.getAvatar():""); }
            User s = userService.getById(o.getStudentId()); m.put("studentName",s!=null?s.getRealName():""); m.put("studentAvatar",s!=null?s.getAvatar():"");
            result.add(m);
        });
        return result;
    }

    private void addReputation(Long userId, int score, String reason) {
        User u = userService.getById(userId);
        if (u==null) return;
        u.setReputation(Math.max(0,Math.min(100,u.getReputation()+score)));
        userService.updateById(u);
        ReputationRecord rr = new ReputationRecord(); rr.setUserId(userId); rr.setChangeScore(score); rr.setReason(reason);
        rr.setType(score>0?1:2); rr.setOperator("system"); rr.setCreateTime(LocalDateTime.now());
        reputationRecordService.save(rr);
    }

    private void sendSystemMessage(Long userId, String title, String content, String type) {
        Message msg = new Message(); msg.setUserId(userId); msg.setTitle(title); msg.setContent(content);
        msg.setType(type); msg.setIsRead(0); msg.setCreateTime(LocalDateTime.now());
        messageService.save(msg);
    }
}
