-- 预设管理员账号（密码: admin123，BCrypt加密后的哈希值）
INSERT INTO users (account, password, nickname, role, is_deleted) VALUES
('admin', '$2a$10$8zkzwXSASql0hbZ16jw.PO.grvG4bOZ3dabjgGJ5ql/kxfPljqlzK', '管理员', 1, 0);

-- 示例板块
INSERT INTO boards (name, description) VALUES
('技术讨论', '技术相关话题讨论'),
('生活分享', '日常生活分享交流');
