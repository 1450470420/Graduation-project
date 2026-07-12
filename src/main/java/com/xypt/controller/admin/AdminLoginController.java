package com.xypt.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xypt.annotation.OperLog;
import com.xypt.common.Result;
import com.xypt.entity.Admin;
import com.xypt.service.AdminService;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员登录、登出Controller
 * 路径前缀：/api/admin
 */
@RestController
@RequestMapping("/api/admin")
public class AdminLoginController {

    @Autowired
    private AdminService adminService;

    /**
     * 管理员登录接口
     * POST /api/admin/login
     * 验证用户名和密码，登录成功后将管理员信息存入Session
     */
    @PostMapping("/login")
    @OperLog("管理员登录")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request, HttpSession session) {
        // 参数校验
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return Result.error("密码不能为空");
        }

        // 查询管理员
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, request.getUsername())
               .eq(Admin::getPassword, request.getPassword())
               .eq(Admin::getStatus, 1);
        Admin admin = adminService.getOne(wrapper);

        if (admin == null) {
            return Result.error("用户名或密码错误");
        }

        // 将管理员信息存入Session（用于权限验证和日志记录）
        session.setAttribute("adminId", admin.getId());
        session.setAttribute("adminUsername", admin.getUsername());
        session.setAttribute("adminRole", admin.getRole());

        // 返回管理员基本信息（不返回密码）
        Map<String, Object> data = new HashMap<>();
        data.put("id", admin.getId());
        data.put("username", admin.getUsername());
        data.put("realName", admin.getRealName());
        data.put("role", admin.getRole());

        return Result.success("登录成功", data);
    }

    /**
     * 管理员退出登录
     * POST /api/admin/logout
     */
    @PostMapping("/logout")
    @OperLog("管理员退出登录")
    public Result<String> logout(HttpSession session) {
        session.invalidate();
        return Result.success("退出成功");
    }

    /**
     * 获取当前登录管理员信息
     * GET /api/admin/info
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getInfo(HttpSession session) {
        Long adminId = (Long) session.getAttribute("adminId");
        if (adminId == null) {
            return Result.unauthorized();
        }
        Admin admin = adminService.getById(adminId);
        if (admin == null) {
            return Result.error("管理员不存在");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", admin.getId());
        data.put("username", admin.getUsername());
        data.put("realName", admin.getRealName());
        data.put("role", admin.getRole());
        return Result.success(data);
    }

    /**
     * 登录请求体
     */
    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
