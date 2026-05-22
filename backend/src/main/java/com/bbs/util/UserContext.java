package com.bbs.util;

/**
 * 基于ThreadLocal的用户上下文
 * <p>在请求线程中存储当前登录用户信息，请求结束后清除</p>
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> ACCOUNT = new ThreadLocal<>();
    private static final ThreadLocal<Integer> ROLE = new ThreadLocal<>();

    /**
     * 设置当前用户ID
     *
     * @param userId 用户ID
     */
    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    /**
     * 获取当前用户ID
     *
     * @return 用户ID
     */
    public static Long getUserId() {
        return USER_ID.get();
    }

    /**
     * 设置当前用户账号
     *
     * @param account 账号
     */
    public static void setAccount(String account) {
        ACCOUNT.set(account);
    }

    /**
     * 获取当前用户账号
     *
     * @return 账号
     */
    public static String getAccount() {
        return ACCOUNT.get();
    }

    /**
     * 设置当前用户角色
     *
     * @param role 角色（0-普通用户，1-管理员）
     */
    public static void setRole(Integer role) {
        ROLE.set(role);
    }

    /**
     * 获取当前用户角色
     *
     * @return 角色值
     */
    public static Integer getRole() {
        return ROLE.get();
    }

    /**
     * 清除当前线程的所有用户上下文信息
     * <p>必须在请求结束后调用，防止内存泄漏</p>
     */
    public static void clear() {
        USER_ID.remove();
        ACCOUNT.remove();
        ROLE.remove();
    }
}
