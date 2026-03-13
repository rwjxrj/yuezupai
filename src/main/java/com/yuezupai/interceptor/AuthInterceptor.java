package com.yuezupai.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuezupai.common.constant.Constants;
import com.yuezupai.common.result.R;
import com.yuezupai.common.result.ResultCode;
import com.yuezupai.util.JwtUtil;
import com.yuezupai.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 鉴权拦截器
 * 从请求头获取Token → 验证 → 放入UserContext → 放行
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthInterceptor(JwtUtil jwtUtil, RedisTemplate<String, Object> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        // 1. 从请求头获取Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeError(response, ResultCode.UNAUTHORIZED);
            return false;
        }

        String token = authHeader.substring(7);

        // 2. 验证JWT格式和签名
        if (!jwtUtil.validateToken(token)) {
            writeError(response, ResultCode.UNAUTHORIZED);
            return false;
        }

        // 3. 检查Redis中是否存在（支持踢下线）
        Long userId = jwtUtil.getUserId(token);
        String redisKey = Constants.REDIS_TOKEN_PREFIX + userId;
        String savedToken = (String) redisTemplate.opsForValue().get(redisKey);

        if (savedToken == null || !savedToken.equals(token)) {
            writeError(response, ResultCode.UNAUTHORIZED);
            return false;
        }

        // 4. 放入线程上下文
        String role = jwtUtil.getRole(token);
        UserContext.setUserId(userId);
        UserContext.setRole(role);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 请求结束清除上下文，防止内存泄漏
        UserContext.clear();
    }

    /** 写错误响应 */
    private void writeError(HttpServletResponse response, ResultCode resultCode) throws Exception {
        response.setStatus(200);  // HTTP状态码统一200，业务状态码在body里
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(R.fail(resultCode)));
    }
}