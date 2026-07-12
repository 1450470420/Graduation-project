package com.xypt.controller.admin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xypt.annotation.OperLog;
import com.xypt.common.PageResult;
import com.xypt.common.Result;
import com.xypt.entity.BalanceRecord;
import com.xypt.entity.OrderRefund;
import com.xypt.entity.Orders;
import com.xypt.entity.User;
import com.xypt.service.BalanceRecordService;
import com.xypt.service.OrderRefundService;
import com.xypt.service.OrdersService;
import com.xypt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
/** 退款申请管理Controller */
@RestController
@RequestMapping("/api/admin/refunds")
public class RefundController {
    @Autowired OrderRefundService refundService;
    @Autowired OrdersService ordersService;
    @Autowired UserService userService;
    @Autowired BalanceRecordService balanceRecordService;
    @GetMapping
    public Result<PageResult<Map<String,Object>>> list(@RequestParam(defaultValue="1") long current,
            @RequestParam(defaultValue="10") long size, @RequestParam(required=false) Integer status) {
        Page<OrderRefund> page = new Page<>(current, size);
        LambdaQueryWrapper<OrderRefund> w = new LambdaQueryWrapper<>();
        if (status != null) w.eq(OrderRefund::getStatus, status);
        w.orderByDesc(OrderRefund::getCreateTime);
        Page<OrderRefund> result = refundService.page(page, w);
        List<Map<String,Object>> records = new ArrayList<>();
        result.getRecords().forEach(r -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id",r.getId()); m.put("orderId",r.getOrderId()); m.put("reason",r.getReason());
            m.put("status",r.getStatus()); m.put("adminRemark",r.getAdminRemark()); m.put("createTime",r.getCreateTime());
            Orders order = ordersService.getById(r.getOrderId());
            m.put("orderNo", order!=null?order.getOrderNo():"");
            m.put("reward", order!=null?order.getReward():0);
            User student = userService.getById(r.getStudentId());
            m.put("studentName", student!=null?student.getRealName():"");
            records.add(m);
        });
        return Result.success(PageResult.of(records, result.getTotal(), current, size));
    }
    @PutMapping("/{id}/approve")
    @OperLog("审核退款申请通过")
    @Transactional
    public Result<String> approve(@PathVariable Long id, @RequestBody Map<String,String> body) {
        OrderRefund r = refundService.getById(id);
        if (r == null) return Result.error("申请不存在");
        if (r.getStatus() != 0) return Result.error("该申请已处理");
        // 1. 更新退款单状态为已通过
        r.setStatus(1);
        r.setAdminRemark(body.getOrDefault("remark", "已通过，退款将处理"));
        refundService.updateById(r);
        // 2. 将关联订单状态改为已取消(5)
        Orders order = ordersService.getById(r.getOrderId());
        if (order != null && order.getStatus() < 5) {
            // 若订单已完成(status=4)，跑腿员已获得赏金，需从跑腿员余额扣除
            if (order.getStatus() == 4 && order.getCourierId() != null && order.getReward() != null) {
                User courier = userService.getById(order.getCourierId());
                if (courier != null) {
                    BigDecimal deduct = order.getReward().min(courier.getBalance());
                    courier.setBalance(courier.getBalance().subtract(deduct));
                    userService.updateById(courier);
                    // 创建跑腿员余额扮扣流水记录
                    BalanceRecord br = new BalanceRecord();
                    br.setUserId(order.getCourierId());
                    br.setAmount(deduct.negate());
                    br.setType(3); // 3=退款扮扣
                    br.setRemark("订单" + order.getOrderNo() + "退款，赏金扣回");
                    br.setOrderId(order.getId());
                    br.setCreateTime(LocalDateTime.now());
                    balanceRecordService.save(br);
                }
            }
            order.setStatus(5);
            order.setCancelReason("退款审核通过，订单已取消");
            ordersService.updateById(order);
        }
        return Result.success("已通过");
    }
    @PutMapping("/{id}/reject")
    @OperLog("驳回退款申请")
    public Result<String> reject(@PathVariable Long id, @RequestBody Map<String,String> body) {
        OrderRefund r = refundService.getById(id);
        if (r==null) return Result.error("申请不存在");
        r.setStatus(2); r.setAdminRemark(body.getOrDefault("remark",""));
        refundService.updateById(r);
        return Result.success("已驳回");
    }
}
