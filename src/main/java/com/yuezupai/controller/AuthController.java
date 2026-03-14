package com.yuezupai.controller;

import com.yuezupai.common.result.R;
import com.yuezupai.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;

    /** 读取当前激活的环境，默认dev */
    @Value("${spring.profiles.active:dev}")
    private String profile;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 微信快捷登录
     * POST /v1/auth/wx-login
     */
    @PostMapping("/wx-login")
    public R<Map<String, Object>> wxLogin(@RequestBody Map<String, String> params) {
        String code = params.get("code");
        if (code == null || code.isBlank()) {
            return R.fail("code不能为空");
        }
        return R.ok(authService.wxLogin(code));
    }

    /**
     * 【开发调试专用】通过userId直接获取Token，生产环境不可用
     * POST /v1/auth/dev-login
     * Body: { "userId": 1 }
     *
     * 使用前先在数据库插入测试用户：
     * INSERT INTO sys_user (openid, nickname, role) VALUES ('test_admin', '测试管理员', 'admin');
     * INSERT INTO sys_user (openid, nickname, role) VALUES ('test_user', '测试用户', 'user');
     */
    @PostMapping("/dev-login")
    public R<Map<String, Object>> devLogin(@RequestBody Map<String, Long> params) {
        if (!"dev".equals(profile)) {
            return R.fail("该接口仅在开发环境可用");
        }
        Long userId = params.get("userId");
        if (userId == null) {
            return R.fail("userId不能为空");
        }
        return R.ok(authService.devLogin(userId));
    }
}