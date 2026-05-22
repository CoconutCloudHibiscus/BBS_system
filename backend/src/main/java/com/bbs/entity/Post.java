package com.bbs.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 帖子实体类
 * <p>对应数据库posts表</p>
 */
@Data
public class Post {

    /** 帖子ID */
    private Long id;

    /** 标题 */
    private String title;

    /** 内容 */
    private String content;

    /** 摘要 */
    private String summary;

    /** 发帖用户ID */
    private Long userId;

    /** 所属板块ID */
    private Long boardId;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 作者昵称（非数据库字段，通过JOIN查询获取） */
    private String authorName;

    /** 作者是否已删除（0-未删除，1-已删除，非数据库字段，通过JOIN查询获取） */
    private Integer authorDeleted;
}
