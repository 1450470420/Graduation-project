package com.xypt.controller.app;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xypt.common.Result;
import com.xypt.entity.*;
import com.xypt.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
/** 跑腿员端专属接口（入驻申请、收益、提现等） */
@RestController
@RequestMapping("/api/app/courier")
public class AppCourierController {
    @Autowired UserService userService;
    @Autowired CourierApplicationService applicationService;
    @Autowired CourierPreferenceService preferenceService;
    @Autowired WithdrawalService withdrawalService;
    @Autowired BalanceRecordService balanceRecordService;
    @Autowired OrdersService ordersService;
    @Autowired EvaluationService evaluationService;
    @Autowired ReputationRecordService reputationRecordService;

    /** 提交入驻申请 POST /api/app/courier/application */
    @PostMapping("/application")
    public Result<String> applyJoin(@RequestBody CourierApplication app, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        User user = userService.getById(userId);
        if (user.getCourierStatus()!=0 && user.getCourierStatus()!=3) return Result.error("已提交申请，请勿重复提交");
        app.setUserId(userId); app.setStatus(0); app.setCreateTime(LocalDateTime.now());
        applicationService.save(app);
        user.setCourierStatus(1); userService.updateById(user);
        return Result.success("申请已提交，等待管理员审核");
    }

    /** 查看入驻申请状态 GET /api/app/courier/application */
    @GetMapping("/application")
    public Result<CourierApplication> getApplication(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        CourierApplication app = applicationService.getOne(
            new LambdaQueryWrapper<CourierApplication>().eq(CourierApplication::getUserId,userId).orderByDesc(CourierApplication::getCreateTime).last("limit 1"));
        return Result.success(app);
    }

    /** 获取接单偏好设置 GET /api/app/courier/preference */
    @GetMapping("/preference")
    public Result<CourierPreference> preference(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        CourierPreference pref = preferenceService.getOne(new LambdaQueryWrapper<CourierPreference>().eq(CourierPreference::getCourierId,userId));
        return Result.success(pref);
    }

    /** 更新接单偏好 PUT /api/app/courier/preference */
    @PutMapping("/preference")
    public Result<String> updatePreference(@RequestBody CourierPreference pref, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        pref.setCourierId(userId);
        CourierPreference existing = preferenceService.getOne(new LambdaQueryWrapper<CourierPreference>().eq(CourierPreference::getCourierId,userId));
        if (existing!=null) { pref.setId(existing.getId()); preferenceService.updateById(pref); }
        else { preferenceService.save(pref); }
        return Result.success("偏好设置已更新");
    }

    /** 申请提现 POST /api/app/courier/withdrawals */
    @PostMapping("/withdrawals")
    @Transactional
    public Result<String> applyWithdrawal(@RequestBody Withdrawal withdrawal, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        User courier = userService.getById(userId);
        if (courier.getBalance()==null||courier.getBalance().compareTo(withdrawal.getAmount())<0) return Result.error("余额不足");
        if (withdrawal.getAmount()==null||withdrawal.getAmount().compareTo(new BigDecimal("50"))<0) return Result.error("最低提现金额为50元");
        withdrawal.setCourierId(userId); withdrawal.setStatus(0); withdrawal.setCreateTime(LocalDateTime.now());
        withdrawalService.save(withdrawal);
        courier.setBalance(courier.getBalance().subtract(withdrawal.getAmount())); userService.updateById(courier);
        BalanceRecord br = new BalanceRecord(); br.setUserId(userId); br.setAmount(withdrawal.getAmount());
        br.setType(2); br.setRemark("申请提现（"+withdrawal.getPaymentMethod()+"）"); br.setCreateTime(LocalDateTime.now());
        balanceRecordService.save(br);
        return Result.success("提现申请已提交，预计1-3个工作日到账");
    }

    /** 提现记录 GET /api/app/courier/withdrawals */
    @GetMapping("/withdrawals")
    public Result<List<Withdrawal>> withdrawals(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        return Result.success(withdrawalService.list(new LambdaQueryWrapper<Withdrawal>().eq(Withdrawal::getCourierId,userId).orderByDesc(Withdrawal::getCreateTime)));
    }

    /** 收益明细 GET /api/app/courier/earnings */
    @GetMapping("/earnings")
    public Result<Map<String,Object>> earnings(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        User courier = userService.getById(userId);
        long totalOrders = ordersService.count(new LambdaQueryWrapper<Orders>().eq(Orders::getCourierId,userId));
        long completedOrders = ordersService.count(new LambdaQueryWrapper<Orders>().eq(Orders::getCourierId,userId).eq(Orders::getStatus,4));
        long goodReviews = evaluationService.count(new LambdaQueryWrapper<Evaluation>().eq(Evaluation::getCourierId,userId).ge(Evaluation::getRating,4));
        double goodRate = completedOrders>0?(double)goodReviews/completedOrders*100:0;
        double completeRate = totalOrders>0?(double)completedOrders/totalOrders*100:0;
        List<BalanceRecord> records = balanceRecordService.list(
            new LambdaQueryWrapper<BalanceRecord>().eq(BalanceRecord::getUserId,userId).orderByDesc(BalanceRecord::getCreateTime).last("limit 30"));
        Map<String,Object> m = new HashMap<>();
        m.put("balance",courier.getBalance()); m.put("totalEarnings",courier.getTotalEarnings());
        m.put("totalOrders",totalOrders); m.put("completedOrders",completedOrders);
        m.put("goodRate",Math.round(goodRate*10.0)/10.0); m.put("completeRate",Math.round(completeRate*10.0)/10.0);
        m.put("records",records);
        return Result.success(m);
    }

    /** 跑腿员接单历史 GET /api/app/courier/orders */
    @GetMapping("/orders")
    public Result<List<Map<String,Object>>> courierOrders(@RequestParam(required=false) Integer status, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        LambdaQueryWrapper<Orders> w = new LambdaQueryWrapper<Orders>().eq(Orders::getCourierId,userId);
        if (status!=null) w.eq(Orders::getStatus,status);
        w.orderByDesc(Orders::getCreateTime);
        List<Orders> orders = ordersService.list(w);
        List<Map<String,Object>> result = new ArrayList<>();
        orders.forEach(o -> {
            Map<String,Object> map = new HashMap<>();
            map.put("id",o.getId()); map.put("orderNo",o.getOrderNo()); map.put("serviceType",o.getServiceType());
            map.put("pickupAddress",o.getPickupAddress()); map.put("deliveryAddress",o.getDeliveryAddress());
            map.put("itemInfo",o.getItemInfo()); map.put("reward",o.getReward()); map.put("status",o.getStatus());
            map.put("createTime",o.getCreateTime()); map.put("grabTime",o.getGrabTime()); map.put("completeTime",o.getCompleteTime());
            User student = userService.getById(o.getStudentId());
            map.put("studentName",student!=null?student.getRealName():""); map.put("studentPhone",student!=null?student.getPhone():"");
            map.put("studentAvatar",student!=null?student.getAvatar():"");
            result.add(map);
        });
        return Result.success(result);
    }

    /** 获取跑腿员信誉分 GET /api/app/courier/reputation */
    @GetMapping("/reputation")
    public Result<Map<String,Object>> reputation(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        User user = userService.getById(userId);
        List<ReputationRecord> records = reputationRecordService.list(
            new LambdaQueryWrapper<ReputationRecord>().eq(ReputationRecord::getUserId,userId).orderByDesc(ReputationRecord::getCreateTime).last("limit 20"));
        Map<String,Object> m = new HashMap<>();
        m.put("score",user.getReputation()); m.put("records",records);
        return Result.success(m);
    }

    /** 跑腿员上报实时位置 PUT /api/app/courier/location
     *  配送中每3秒调一次，供学生端实时展示跑腿员轨迹 */
    @PutMapping("/location")
    public Result<String> updateLocation(@RequestBody Map<String,Double> body, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        Double lat = body.get("lat");
        Double lng = body.get("lng");
        if (lat==null||lng==null) return Result.error("缺少经纬度参数");
        User courier = userService.getById(userId);
        if (courier==null) return Result.error("用户不存在");
        courier.setCurrentLat(lat);
        courier.setCurrentLng(lng);
        courier.setLocationUpdateTime(LocalDateTime.now());
        userService.updateById(courier);
        return Result.success("位置已更新");
    }

    /** 查看学生给跑腿员的评价 GET /api/app/courier/reviews */
    @GetMapping("/reviews")
    public Result<List<Map<String,Object>>> reviews(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId==null) return Result.unauthorized();
        List<Evaluation> evals = evaluationService.list(
            new LambdaQueryWrapper<Evaluation>().eq(Evaluation::getCourierId,userId).orderByDesc(Evaluation::getCreateTime));
        List<Map<String,Object>> result = new ArrayList<>();
        evals.forEach(e -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id",e.getId()); m.put("rating",e.getRating()); m.put("content",e.getContent());
            m.put("images",e.getImages()); m.put("createTime",e.getCreateTime());
            User student = userService.getById(e.getStudentId());
            m.put("studentName",student!=null?student.getRealName():""); m.put("studentAvatar",student!=null?student.getAvatar():"");
            result.add(m);
        });
        return Result.success(result);
    }
}
