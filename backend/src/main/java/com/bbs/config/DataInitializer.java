package com.bbs.config;

import com.bbs.mapper.UserMapper;
import com.bbs.entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 数据初始化器
 * <p>应用启动时自动检测数据库表是否存在，不存在则执行建表脚本；
 * 然后确保管理员账号存在且密码正确，确保初始板块数据存在</p>
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final List<String> REQUIRED_TABLES = List.of("users", "boards", "posts", "comments");

    public DataInitializer(JdbcTemplate jdbcTemplate, UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public void run(String... args) {
        initSchema();
        initAdmin();
        initBoards();
    }

    /**
     * 检测数据库表是否存在，不存在则执行schema.sql建表
     */
    private void initSchema() {
        try {
            // 查询当前数据库中所有表名
            List<String> existingTables = jdbcTemplate.queryForList(
                    "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = DATABASE()",
                    String.class
            );

            boolean missingTable = REQUIRED_TABLES.stream()
                    .anyMatch(table -> !existingTables.contains(table));

            if (missingTable) {
                System.out.println("[DataInitializer] 检测到数据库缺少表，开始执行建表脚本...");
                ClassPathResource resource = new ClassPathResource("schema.sql");
                String sql = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

                // 按分号分割并逐条执行（跳过空语句）
                String[] statements = sql.split(";");
                for (String stmt : statements) {
                    String trimmed = stmt.trim();
                    if (!trimmed.isEmpty()) {
                        jdbcTemplate.execute(trimmed);
                    }
                }
                System.out.println("[DataInitializer] 建表脚本执行完成");
            } else {
                System.out.println("[DataInitializer] 数据库表检测通过，所有表已存在");
            }
        } catch (Exception e) {
            System.err.println("[DataInitializer] 建表脚本执行失败: " + e.getMessage());
            throw new RuntimeException("数据库初始化失败", e);
        }
    }

    /**
     * 初始化管理员账号
     * <p>如果admin账号不存在则创建，存在则确保密码为admin123</p>
     */
    private void initAdmin() {
        User admin = userMapper.findByAccount("admin");
        String encodedPassword = passwordEncoder.encode("admin123");

        if (admin == null) {
            User user = new User();
            user.setAccount("admin");
            user.setPassword(encodedPassword);
            user.setNickname("管理员");
            user.setRole(1);
            user.setIsDeleted(0);
            userMapper.insert(user);
            System.out.println("[DataInitializer] 管理员账号已创建: admin / admin123");
        } else {
            if (!passwordEncoder.matches("admin123", admin.getPassword())) {
                userMapper.updatePassword(admin.getId(), encodedPassword);
                System.out.println("[DataInitializer] 管理员密码已更新");
            }
        }
    }

    /**
     * 初始化示例板块
     * <p>如果板块表为空则插入示例板块数据</p>
     */
    private void initBoards() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM boards", Integer.class);
        if (count != null && count == 0) {
            jdbcTemplate.update("INSERT INTO boards (name, description) VALUES ('技术讨论', '技术相关话题讨论')");
            jdbcTemplate.update("INSERT INTO boards (name, description) VALUES ('生活分享', '日常生活分享交流')");
            System.out.println("[DataInitializer] 示例板块已创建");
        }
    }
}
