package com.bbs.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * <p>对应数据库users表</p>
 */
@Data
public class User {

    /** 用户ID */
    private Long id;

    /** 账号 */
    private String account;

    /** 密码（BCrypt加密） */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 角色（0-普通用户，1-管理员） */
    private Integer role;

    /** 是否已删除（0-未删除，1-已删除） */
    private Integer isDeleted;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
