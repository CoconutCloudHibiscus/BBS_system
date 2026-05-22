package com.bbs.controller;

import com.bbs.common.Result;
import com.bbs.entity.User;
import com.bbs.service.UserService;
import com.bbs.util.UserContext;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * <p>处理用户信息查询、修改和删除等接口</p>
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户信息
     *
     * @return 当前用户信息
     */
    @GetMapping("/me")
    public Result<User> getCurrentUser() {
        Long userId = UserContext.getUserId();
        User user = userService.getCurrentUser(userId);
        return Result.success(user);
    }

    /**
     * 修改昵称
     *
     * @param request 昵称修改请求体
     * @return 操作结果
     */
    @PutMapping("/nickname")
    public Result<Void> updateNickname(@Validated @RequestBody UpdateNicknameRequest request) {
        Long userId = UserContext.getUserId();
        userService.updateNickname(userId, request.getNickname());
        return Result.success("昵称修改成功", null);
    }

    /**
     * 删除账户
     * <p>硬删除用户，级联删除用户的所有帖子，Token加入黑名单</p>
     *
     * @return 操作结果
     */
    @DeleteMapping("/me")
    public Result<Void> deleteAccount(@RequestHeader("Authorization") String authHeader) {
        Long userId = UserContext.getUserId();
        String token = authHeader.substring(7);
        userService.deleteAccount(userId, token);
        return Result.success("账户已删除", null);
    }

    /**
     * 修改昵称请求体
     */
    @Data
    public static class UpdateNicknameRequest {
        /** 新昵称 */
        @NotBlank(message = "昵称不能为空")
        private String nickname;
    }
}
