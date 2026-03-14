package com.yuezupai.service;

import java.util.Map;

public interface AuthService {

    /** 微信登录 */
    Map<String, Object> wxLogin(String code);

    /** 开发环境测试登录（通过userId直接获取Token） */
    Map<String, Object> devLogin(Long userId);
}