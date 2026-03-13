package com.yuezupai.controller;

import com.yuezupai.common.result.R;
import com.yuezupai.entity.SysUser;
import com.yuezupai.mapper.SysUserMapper;
import com.yuezupai.util.UserContext;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    private final SysUserMapper userMapper;

    public UserController(SysUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 获取当前用户信息
     * GET /v1/user/info
     */
    @GetMapping("/info")
    public R<SysUser> getUserInfo() {
        Long userId = UserContext.getUserId();
        SysUser user = userMapper.selectById(userId);
        // 不返回openid（敏感信息）
        user.setOpenid(null);
        return R.ok(user);
    }

    /**
     * 更新昵称头像
     * PUT /v1/user/profile
     * Body: { "nickname": "xxx", "avatarUrl": "xxx" }
     */
    @PutMapping("/profile")
    public R<Void> updateProfile(@RequestBody Map<String, String> params) {
        Long userId = UserContext.getUserId();
        SysUser user = new SysUser();
        user.setUserId(userId);

        if (params.containsKey("nickname")) {
            user.setNickname(params.get("nickname"));
        }
        if (params.containsKey("avatarUrl")) {
            user.setAvatarUrl(params.get("avatarUrl"));
        }

        userMapper.updateById(user);
        return R.ok();
    }
}