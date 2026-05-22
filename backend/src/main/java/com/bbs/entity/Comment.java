package com.bbs.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论实体类
 * <p>对应数据库comments表</p>
 */
@Data
public class Comment {

    /** 评论ID */
    private Long id;

    /** 评论内容 */
    private String content;

    /** 所属帖子ID */
    private Long postId;

    /** 评论用户ID */
    private Long userId;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 作者昵称（非数据库字段，通过JOIN查询获取） */
    private String authorName;

    /** 作者是否已删除（0-未删除，1-已删除，非数据库字段，通过JOIN查询获取） */
    private Integer authorDeleted;
}
