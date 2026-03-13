package com.yuezupai.util;

/**
 * 当前登录用户上下文
 * 拦截器里set，Controller/Service里get，请求结束remove
 */
public class UserContext {

    private UserContext() {}

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static void setRole(String role) {
        ROLE.set(role);
    }

    public static String getRole() {
        return ROLE.get();
    }

    /** 请求结束后必须清除，防止内存泄漏 */
    public static void clear() {
        USER_ID.remove();
        ROLE.remove();
    }
}