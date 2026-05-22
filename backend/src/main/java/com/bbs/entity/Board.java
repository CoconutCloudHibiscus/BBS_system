package com.bbs.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 板块实体类
 * <p>对应数据库boards表</p>
 */
@Data
public class Board {

    /** 板块ID */
    private Long id;

    /** 板块名称 */
    private String name;

    /** 板块描述 */
    private String description;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
