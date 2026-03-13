package com.yuezupai.service;

import java.util.Map;

public interface AuthService {
    /**
     * 微信登录
     * @param code 微信临时登录凭证
     * @return 包含 token 和 userInfo
     */
    Map<String, Object> wxLogin(String code);
}