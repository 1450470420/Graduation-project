package com.xypt.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xypt.annotation.OperLog;
import com.xypt.common.PageResult;
import com.xypt.common.Result;
import com.xypt.entity.User;
import com.xypt.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理Controller（学生和跑腿员管理）
 * 路径前缀：/api/admin/users
 */
@RestController
@RequestMapping("/api/admin/users")
public class UserManageController {

    @Autowired
    private UserService userService;

    /**
     * 获取学生列表（分页+搜索）
     * GET /api/admin/users/students
     * 支持按用户名、姓名、手机号、学号搜索
     */
    @GetMapping("/students")
    public Result<PageResult<User>> getStudents(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 只查学生
        wrapper.eq(User::getUserType, 1);
        // 关键字搜索（用户名/姓名/手机号/学号）
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getRealName, keyword)
                    .or().like(User::getPhone, keyword)
                    .or().like(User::getStudentId, keyword));
        }
        // 状态筛选
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = userService.page(page, wrapper);
        // 清除密码字段，不返回给前端
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(PageResult.of(result));
    }

    /**
     * 获取跑腿员列表（分页+搜索）
     * GET /api/admin/users/couriers
     */
    @GetMapping("/couriers")
    public Result<PageResult<User>> getCouriers(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer courierStatus) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 只查跑腿员
        wrapper.eq(User::getUserType, 2);
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getRealName, keyword)
                    .or().like(User::getPhone, keyword));
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        if (courierStatus != null) {
            wrapper.eq(User::getCourierStatus, courierStatus);
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = userService.page(page, wrapper);
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(PageResult.of(result));
    }

    /**
     * 获取用户详情
     * GET /api/admin/users/{id}
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 封禁用户账号
     * PUT /api/admin/users/{id}/ban
     */
    @PutMapping("/{id}/ban")
    @OperLog("封禁用户账号")
    public Result<String> banUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setStatus(2); // 2=封禁
        userService.updateById(user);
        return Result.success("封禁成功");
    }

    /**
     * 解封用户账号
     * PUT /api/admin/users/{id}/unban
     */
    @PutMapping("/{id}/unban")
    @OperLog("解封用户账号")
    public Result<String> unbanUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setStatus(1); // 1=正常
        userService.updateById(user);
        return Result.success("解封成功");
    }

    /**
     * 更新跑腿员接单权限状态
     * PUT /api/admin/users/{id}/courier-status
     */
    @PutMapping("/{id}/courier-status")
    @OperLog("更新跑腿员接单权限")
    public Result<String> updateCourierStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer courierStatus = body.get("courierStatus");
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setCourierStatus(courierStatus);
        userService.updateById(user);
        return Result.success("更新成功");
    }
}
