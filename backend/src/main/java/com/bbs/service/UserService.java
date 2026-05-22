package com.bbs.service;

import com.bbs.common.BusinessException;
import com.bbs.entity.User;
import com.bbs.mapper.CommentMapper;
import com.bbs.mapper.PostMapper;
import com.bbs.mapper.UserMapper;
import com.bbs.util.JwtUtil;
import com.bbs.util.TokenBlacklist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务类
 * <p>处理用户注册、登录、注销、信息修改等业务逻辑</p>
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklist tokenBlacklist;

    /** BCrypt密码加密器 */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户注册
     * <p>检查账号是否已存在，加密密码后保存用户，自动生成Token返回</p>
     *
     * @param account  账号
     * @param password 密码
     * @param nickname 昵称
     * @return 包含用户信息和Token的Map
     */
    @Transactional
    public Map<String, Object> register(String account, String password, String nickname) {
        // 检查账号是否已存在（包含已删除的）
        User existingUser = userMapper.findByAccountIncludeDeleted(account);
        if (existingUser != null) {
            if (existingUser.getIsDeleted() == 1) {
                // 已删除的账号，先物理删除再重新注册
                userMapper.hardDeleteById(existingUser.getId());
            } else {
                throw new BusinessException("账号已存在");
            }
        }

        // 创建新用户
        User user = new User();
        user.setAccount(account);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        user.setRole(0); // 默认普通用户
        user.setIsDeleted(0);

        userMapper.insert(user);

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getAccount(), user.getRole());

        // 返回用户信息和Token
        Map<String, Object> result = new HashMap<>();
        result.put("user", sanitizeUser(user));
        result.put("token", token);
        return result;
    }

    /**
     * 用户登录
     * <p>验证账号密码，返回用户信息和Token</p>
     *
     * @param account  账号
     * @param password 密码
     * @return 包含用户信息和Token的Map
     */
    public Map<String, Object> login(String account, String password) {
        User user = userMapper.findByAccount(account);
        if (user == null) {
            throw new BusinessException("账号不存在");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getAccount(), user.getRole());

        // 返回用户信息和Token
        Map<String, Object> result = new HashMap<>();
        result.put("user", sanitizeUser(user));
        result.put("token", token);
        return result;
    }

    /**
     * 用户注销
     * <p>将Token加入黑名单，使其失效</p>
     *
     * @param token JWT Token
     */
    public void logout(String token) {
        long remaining = jwtUtil.getRemainingExpiration(token);
        tokenBlacklist.addToBlacklist(token, remaining);
    }

    /**
     * 获取当前用户信息
     *
     * @param userId 用户ID
     * @return 用户实体（不含密码）
     */
    public User getCurrentUser(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return sanitizeUser(user);
    }

    /**
     * 修改用户昵称
     *
     * @param userId   用户ID
     * @param nickname 新昵称
     */
    public void updateNickname(Long userId, String nickname) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        userMapper.updateNickname(userId, nickname);
    }

    /**
     * 删除用户账户
     * <p>硬删除用户，级联删除用户的所有帖子（帖子删了评论也跟着删），
     * 用户在其他帖子下的评论保留，显示"已注销用户"</p>
     *
     * @param userId 用户ID
     * @param token  当前Token（加入黑名单）
     */
    @Transactional
    public void deleteAccount(Long userId, String token) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 级联删除用户的所有帖子（帖子删了评论也跟着删）
        postMapper.deleteByUserId(userId);

        // 硬删除用户（评论保留，显示"已注销用户"）
        userMapper.hardDeleteById(userId);

        // Token加入黑名单
        if (token != null) {
            long remaining = jwtUtil.getRemainingExpiration(token);
            tokenBlacklist.addToBlacklist(token, remaining);
        }
    }

    /**
     * 清除用户敏感信息（密码）
     *
     * @param user 用户实体
     * @return 不含密码的用户实体
     */
    private User sanitizeUser(User user) {
        user.setPassword(null);
        return user;
    }
}
