package com.bbs.controller;

import com.bbs.common.Result;
import com.bbs.service.UserService;
import com.bbs.util.UserContext;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 * <p>处理用户注册、登录、注销等认证相关接口</p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     * <p>注册成功后自动登录，返回用户信息和Token</p>
     *
     * @param request 注册请求体（account, password, nickname）
     * @return 包含用户信息和Token的Result
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Validated @RequestBody RegisterRequest request) {
        Map<String, Object> data = userService.register(request.getAccount(), request.getPassword(), request.getNickname());
        return Result.success("注册成功", data);
    }

    /**
     * 用户登录
     *
     * @param request 登录请求体（account, password）
     * @return 包含用户信息和Token的Result
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginRequest request) {
        Map<String, Object> data = userService.login(request.getAccount(), request.getPassword());
        return Result.success("登录成功", data);
    }

    /**
     * 用户注销
     * <p>将当前Token加入黑名单，使其失效</p>
     *
     * @return 操作结果
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.success("注销成功", null);
        }
        String token = authHeader.substring(7);
        userService.logout(token);
        return Result.success("注销成功", null);
    }

    /**
     * 注册请求体
     */
    @Data
    public static class RegisterRequest {
        /** 账号 */
        @NotBlank(message = "账号不能为空")
        private String account;

        /** 密码 */
        @NotBlank(message = "密码不能为空")
        private String password;

        /** 昵称 */
        @NotBlank(message = "昵称不能为空")
        private String nickname;
    }

    /**
     * 登录请求体
     */
    @Data
    public static class LoginRequest {
        /** 账号 */
        @NotBlank(message = "账号不能为空")
        private String account;

        /** 密码 */
        @NotBlank(message = "密码不能为空")
        private String password;
    }
}
