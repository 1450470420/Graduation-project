package com.xypt.controller.admin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xypt.annotation.OperLog;
import com.xypt.common.PageResult;
import com.xypt.common.Result;
import com.xypt.entity.CourierApplication;
import com.xypt.entity.User;
import com.xypt.service.CourierApplicationService;
import com.xypt.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
/** 跑腿员入驻申请管理Controller */
@RestController
@RequestMapping("/api/admin/courier-applications")
public class CourierApplicationController {
    @Autowired CourierApplicationService applicationService;
    @Autowired UserService userService;
    /** 申请列表（分页+搜索） */
    @GetMapping
    public Result<PageResult<Map<String,Object>>> list(
            @RequestParam(defaultValue="1") long current,
            @RequestParam(defaultValue="10") long size,
            @RequestParam(required=false) String keyword,
            @RequestParam(required=false) Integer status) {
        Page<CourierApplication> page = new Page<>(current, size);
        LambdaQueryWrapper<CourierApplication> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like(CourierApplication::getRealName, keyword).or().like(CourierApplication::getPhone, keyword).or().like(CourierApplication::getStudentId, keyword);
        }
        if (status != null) wrapper.eq(CourierApplication::getStatus, status);
        wrapper.orderByDesc(CourierApplication::getCreateTime);
        Page<CourierApplication> result = applicationService.page(page, wrapper);
        // 关联查询用户名
        List<Map<String,Object>> records = new ArrayList<>();
        result.getRecords().forEach(app -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id", app.getId()); m.put("realName", app.getRealName());
            m.put("studentId", app.getStudentId()); m.put("phone", app.getPhone());
            m.put("idCardNo", app.getIdCardNo()); m.put("remark", app.getRemark());
            m.put("status", app.getStatus()); m.put("rejectReason", app.getRejectReason());
            m.put("createTime", app.getCreateTime());
            User user = userService.getById(app.getUserId());
            m.put("username", user != null ? user.getUsername() : "");
            records.add(m);
        });
        return Result.success(PageResult.of(records, result.getTotal(), current, size));
    }
    /** 审核通过 */
    @PutMapping("/{id}/approve")
    @OperLog("审核跑腿员申请通过")
    public Result<String> approve(@PathVariable Long id) {
        CourierApplication app = applicationService.getById(id);
        if (app == null) return Result.error("申请不存在");
        app.setStatus(1);
        applicationService.updateById(app);
        // 更新用户跑腿员状态
        User user = userService.getById(app.getUserId());
        if (user != null) { user.setCourierStatus(2); userService.updateById(user); }
        return Result.success("审核通过");
    }
    /** 审核驳回 */
    @PutMapping("/{id}/reject")
    @OperLog("驳回跑腿员申请")
    public Result<String> reject(@PathVariable Long id, @RequestBody Map<String,String> body) {
        CourierApplication app = applicationService.getById(id);
        if (app == null) return Result.error("申请不存在");
        app.setStatus(2); app.setRejectReason(body.get("reason"));
        applicationService.updateById(app);
        User user = userService.getById(app.getUserId());
        if (user != null) { user.setCourierStatus(3); userService.updateById(user); }
        return Result.success("已驳回");
    }
}
