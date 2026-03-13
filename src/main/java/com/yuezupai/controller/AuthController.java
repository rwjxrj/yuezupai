package com.yuezupai.controller;

import com.yuezupai.common.result.R;
import com.yuezupai.service.AuthService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 微信快捷登录
     * POST /v1/auth/wx-login
     * Body: { "code": "xxx" }
     */
    @PostMapping("/wx-login")
    public R<Map<String, Object>> wxLogin(@RequestBody Map<String, String> params) {
        String code = params.get("code");
        if (code == null || code.isBlank()) {
            return R.fail("code不能为空");
        }
        return R.ok(authService.wxLogin(code));
    }
}