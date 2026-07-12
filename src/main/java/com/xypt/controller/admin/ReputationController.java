package com.xypt.controller.admin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xypt.annotation.OperLog;
import com.xypt.common.PageResult;
import com.xypt.common.Result;
import com.xypt.entity.ReputationRecord;
import com.xypt.entity.User;
import com.xypt.service.ReputationRecordService;
import com.xypt.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;
/** 信誉分管理Controller */
@RestController
@RequestMapping("/api/admin/reputation")
public class ReputationController {
    @Autowired ReputationRecordService recordService;
    @Autowired UserService userService;
    @GetMapping("/records")
    public Result<PageResult<Map<String,Object>>> list(@RequestParam(defaultValue="1") long current,
            @RequestParam(defaultValue="10") long size, @RequestParam(required=false) Long userId) {
        Page<ReputationRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<ReputationRecord> w = new LambdaQueryWrapper<>();
        if (userId!=null) w.eq(ReputationRecord::getUserId, userId);
        w.orderByDesc(ReputationRecord::getCreateTime);
        Page<ReputationRecord> result = recordService.page(page, w);
        List<Map<String,Object>> records = new ArrayList<>();
        result.getRecords().forEach(r -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id",r.getId()); m.put("changeScore",r.getChangeScore()); m.put("reason",r.getReason());
            m.put("type",r.getType()); m.put("operator",r.getOperator()); m.put("createTime",r.getCreateTime());
            User user = userService.getById(r.getUserId());
            m.put("username", user!=null?user.getUsername():""); m.put("realName", user!=null?user.getRealName():"");
            records.add(m);
        });
        return Result.success(PageResult.of(records, result.getTotal(), current, size));
    }
    @PutMapping("/adjust")
    @OperLog("调整用户信誉分")
    public Result<String> adjust(@RequestBody Map<String,Object> body, HttpSession session) {
        Long userId = Long.parseLong(body.get("userId").toString());
        Integer score = Integer.parseInt(body.get("score").toString());
        String reason = body.getOrDefault("reason","").toString();
        User user = userService.getById(userId);
        if (user==null) return Result.error("用户不存在");
        user.setReputation(Math.max(0, Math.min(100, user.getReputation()+score)));
        userService.updateById(user);
        ReputationRecord record = new ReputationRecord();
        record.setUserId(userId); record.setChangeScore(score); record.setReason(reason);
        record.setType(score>0?1:2); record.setOperator(session.getAttribute("adminUsername").toString());
        record.setCreateTime(LocalDateTime.now());
        recordService.save(record);
        return Result.success("调整成功");
    }
}
