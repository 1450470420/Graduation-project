package com.xypt.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xypt.common.Result;
import com.xypt.entity.User;
import com.xypt.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 小程序端认证Controller（学生和跑腿员共用）
 * 包含注册、登录、退出功能
 * 路径前缀：/api/app/auth
 */
@RestController
@RequestMapping("/api/app/auth")
public class AppAuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     * POST /api/app/auth/register
     * 学生和跑腿员都通过此接口注册
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterRequest request) {
        // 参数校验
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            return Result.error("密码长度不能少于6位");
        }
        if (request.getPhone() == null || !request.getPhone().matches("1[3-9]\\d{9}")) {
            return Result.error("请输入正确的手机号");
        }

        // 检查用户名是否已存在
        long count = userService.count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (count > 0) {
            return Result.error("用户名已存在，请换一个");
        }

        // 检查手机号是否已注册
        long phoneCount = userService.count(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, request.getPhone()));
        if (phoneCount > 0) {
            return Result.error("该手机号已注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword()); // 明文存储
        user.setPhone(request.getPhone());
        user.setUserType(request.getUserType() != null ? request.getUserType() : 1); // 默认学生
        user.setStatus(1);
        user.setReputation(100);
        userService.save(user);

        return Result.success("注册成功");
    }

    /**
     * 用户登录
     * POST /api/app/auth/login
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request, HttpSession session) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return Result.error("用户名或密码不能为空");
        }

        // 支持用户名或手机号登录
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .and(w -> w.eq(User::getUsername, request.getUsername())
                        .or().eq(User::getPhone, request.getUsername()))
                .eq(User::getPassword, request.getPassword()));

        if (user == null) {
            return Result.error("用户名或密码错误");
        }

        if (user.getStatus() == 2) {
            return Result.error("账号已被封禁，请联系客服");
        }

        // 存入Session
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("userType", user.getUserType());

        // 返回用户信息
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("phone", user.getPhone());
        data.put("avatar", user.getAvatar());
        data.put("userType", user.getUserType());
        data.put("reputation", user.getReputation());
        data.put("balance", user.getBalance());
        data.put("courierStatus", user.getCourierStatus());
        data.put("isCertified", user.getIsCertified());

        return Result.success("登录成功", data);
    }

    /**
     * 退出登录
     * POST /api/app/auth/logout
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpSession session) {
        session.invalidate();
        return Result.success("退出成功");
    }

    /**
     * 修改密码
     * PUT /api/app/auth/password
     */
    @PutMapping("/password")
    public Result<String> changePassword(@RequestBody Map<String, String> body, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.unauthorized();

        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");

        User user = userService.getById(userId);
        if (!user.getPassword().equals(oldPassword)) {
            return Result.error("原密码错误");
        }
        if (newPassword == null || newPassword.length() < 6) {
            return Result.error("新密码长度不能少于6位");
        }

        user.setPassword(newPassword);
        userService.updateById(user);
        return Result.success("密码修改成功");
    }

    /** 注册请求体 */
    @Data
    public static class RegisterRequest {
        private String username;
        private String password;
        private String phone;
        /** 1=学生 2=跑腿员 */
        private Integer userType;
    }

    /** 登录请求体 */
    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
