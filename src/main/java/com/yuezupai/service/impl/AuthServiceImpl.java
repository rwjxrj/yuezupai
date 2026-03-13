package com.yuezupai.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuezupai.common.constant.Constants;
import com.yuezupai.common.exception.BusinessException;
import com.yuezupai.entity.SysUser;
import com.yuezupai.mapper.SysUserMapper;
import com.yuezupai.service.AuthService;
import com.yuezupai.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

    public AuthServiceImpl(SysUserMapper userMapper, JwtUtil jwtUtil,
                           RedisTemplate<String, Object> redisTemplate) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Map<String, Object> wxLogin(String code) {
        // 1. 用code换openid
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appid, secret, code
        );
        String response = HttpUtil.get(url);
        JSONObject json = JSONUtil.parseObj(response);
        log.info("微信登录返回: {}", response);

        String openid = json.getStr("openid");
        if (openid == null) {
            throw new BusinessException("微信登录失败: " + json.getStr("errmsg"));
        }

        // 2. 查库，有则登录，无则注册
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getOpenid, openid)
        );

        boolean isNewUser = false;
        if (user == null) {
            user = new SysUser();
            user.setOpenid(openid);
            user.setNickname("微信用户");
            user.setIsVip(0);
            user.setRole(Constants.ROLE_USER);
            userMapper.insert(user);
            isNewUser = true;
            log.info("新用户注册: userId={}", user.getUserId());
        }

        // 3. 生成JWT
        String token = jwtUtil.generateToken(user.getUserId(), user.getRole());

        // 4. 存Redis（支持单设备登录，新Token会覆盖旧Token）
        String redisKey = Constants.REDIS_TOKEN_PREFIX + user.getUserId();
        redisTemplate.opsForValue().set(redisKey, token, Constants.TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);

        // 5. 组装返回
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("isNewUser", isNewUser);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getUserId());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("avatarUrl", user.getAvatarUrl());
        userInfo.put("isVip", user.getIsVip());
        userInfo.put("role", user.getRole());
        userInfo.put("phone", user.getPhone());
        result.put("userInfo", userInfo);

        return result;
    }
}