package com.bbs.config;

import com.bbs.mapper.UserMapper;
import com.bbs.entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 * <p>应用启动时自动确保管理员账号存在且密码正确</p>
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DataInitializer(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void run(String... args) {
        initAdmin();
    }

    /**
     * 初始化管理员账号
     * <p>如果admin账号不存在则创建，存在则确保密码为admin123</p>
     */
    private void initAdmin() {
        User admin = userMapper.findByAccount("admin");
        String encodedPassword = passwordEncoder.encode("admin123");

        if (admin == null) {
            // 创建管理员
            User user = new User();
            user.setAccount("admin");
            user.setPassword(encodedPassword);
            user.setNickname("管理员");
            user.setRole(1);
            user.setIsDeleted(0);
            userMapper.insert(user);
            System.out.println("[DataInitializer] 管理员账号已创建: admin / admin123");
        } else {
            // 确保密码正确（更新为正确的哈希）
            if (!passwordEncoder.matches("admin123", admin.getPassword())) {
                userMapper.updatePassword(admin.getId(), encodedPassword);
                System.out.println("[DataInitializer] 管理员密码已更新");
            }
        }
    }
}
