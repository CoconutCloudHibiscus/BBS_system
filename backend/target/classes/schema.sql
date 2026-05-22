CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    account VARCHAR(50) NOT NULL UNIQUE COMMENT '账号，仅字母数字，唯一',
    password VARCHAR(255) NOT NULL COMMENT '密码，BCrypt加密',
    nickname VARCHAR(50) NOT NULL COMMENT '昵称',
    role TINYINT NOT NULL DEFAULT 0 COMMENT '角色：0=普通用户, 1=管理员',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0=正常, 1=已删除',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE IF NOT EXISTS boards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL UNIQUE COMMENT '板块名称，唯一',
    description VARCHAR(500) DEFAULT NULL COMMENT '板块描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='板块表';

CREATE TABLE IF NOT EXISTS posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(255) NOT NULL COMMENT '帖子标题',
    content TEXT NOT NULL COMMENT '帖子内容，纯文本',
    summary VARCHAR(100) DEFAULT NULL COMMENT '摘要，自动截取前100字符',
    user_id BIGINT NOT NULL COMMENT '发帖用户ID',
    board_id BIGINT NOT NULL COMMENT '所属板块ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    INDEX idx_posts_board_id (board_id),
    INDEX idx_posts_user_id (user_id),
    INDEX idx_posts_created_at (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子表';

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    content TEXT NOT NULL COMMENT '评论内容',
    post_id BIGINT NOT NULL COMMENT '所属帖子ID',
    user_id BIGINT NOT NULL COMMENT '评论用户ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    INDEX idx_comments_post_id (post_id),
    INDEX idx_comments_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';
