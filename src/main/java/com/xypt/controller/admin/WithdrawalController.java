package com.xypt.controller.admin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xypt.annotation.OperLog;
import com.xypt.common.PageResult;
import com.xypt.common.Result;
import com.xypt.entity.BalanceRecord;
import com.xypt.entity.User;
import com.xypt.entity.Withdrawal;
import com.xypt.service.BalanceRecordService;
import com.xypt.service.UserService;
import com.xypt.service.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;
/** 提现申请管理Controller */
@RestController
@RequestMapping("/api/admin/withdrawals")
public class WithdrawalController {
    @Autowired WithdrawalService withdrawalService;
    @Autowired UserService userService;
    @Autowired BalanceRecordService balanceRecordService;
    @GetMapping
    public Result<PageResult<Map<String,Object>>> list(@RequestParam(defaultValue="1") long current,
            @RequestParam(defaultValue="10") long size, @RequestParam(required=false) Integer status) {
        Page<Withdrawal> page = new Page<>(current, size);
        LambdaQueryWrapper<Withdrawal> w = new LambdaQueryWrapper<>();
        if (status != null) w.eq(Withdrawal::getStatus, status);
        w.orderByDesc(Withdrawal::getCreateTime);
        Page<Withdrawal> result = withdrawalService.page(page, w);
        List<Map<String,Object>> records = new ArrayList<>();
        result.getRecords().forEach(wi -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id",wi.getId()); m.put("amount",wi.getAmount()); m.put("paymentMethod",wi.getPaymentMethod());
            m.put("accountInfo",wi.getAccountInfo()); m.put("status",wi.getStatus()); m.put("adminRemark",wi.getAdminRemark()); m.put("createTime",wi.getCreateTime());
            User courier = userService.getById(wi.getCourierId());
            m.put("courierName", courier!=null?courier.getRealName():""); m.put("courierUsername", courier!=null?courier.getUsername():"");
            records.add(m);
        });
        return Result.success(PageResult.of(records, result.getTotal(), current, size));
    }
    @PutMapping("/{id}/approve")
    @OperLog("审核提现申请通过")
    public Result<String> approve(@PathVariable Long id, @RequestBody Map<String,String> body) {
        Withdrawal wi = withdrawalService.getById(id);
        if (wi==null) return Result.error("申请不存在");
        wi.setStatus(1); wi.setAdminRemark(body.getOrDefault("remark","已通过，请注意查收"));
        withdrawalService.updateById(wi);
        return Result.success("已通过");
    }
    @PutMapping("/{id}/reject")
    @OperLog("驳回提现申请")
    @Transactional
    public Result<String> reject(@PathVariable Long id, @RequestBody Map<String,String> body) {
        Withdrawal wi = withdrawalService.getById(id);
        if (wi == null) return Result.error("申请不存在");
        if (wi.getStatus() != 0) return Result.error("该申请已处理");
        wi.setStatus(2);
        wi.setAdminRemark(body.getOrDefault("remark", ""));
        withdrawalService.updateById(wi);
        // 退还余额给跑腿员
        User courier = userService.getById(wi.getCourierId());
        if (courier != null && wi.getAmount() != null) {
            courier.setBalance(courier.getBalance().add(wi.getAmount()));
            userService.updateById(courier);
            // 创建余额恢复流水记录
            BalanceRecord br = new BalanceRecord();
            br.setUserId(wi.getCourierId());
            br.setAmount(wi.getAmount());
            br.setType(2); // 2=驳回退回
            br.setRemark("提现申请驳回，金额已退回账户");
            br.setCreateTime(LocalDateTime.now());
            balanceRecordService.save(br);
        }
        return Result.success("已驳回");
    }
}
