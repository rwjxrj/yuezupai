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
            log.warn("[AUTH] 失败-第1步: Header缺失或格式错误, authHeader={}", authHeader);
            writeError(response, ResultCode.UNAUTHORIZED);
            return false;
        }

        String token = authHeader.substring(7);

        // 2. 验证JWT格式和签名
        if (!jwtUtil.validateToken(token)) {
            log.warn("[AUTH] 失败-第2步: JWT签名验证不通过");
            writeError(response, ResultCode.UNAUTHORIZED);
            return false;
        }

        // 3. 检查Redis中是否存在
        Long userId = jwtUtil.getUserId(token);
        String redisKey = Constants.REDIS_TOKEN_PREFIX + userId;
        Object redisValue = redisTemplate.opsForValue().get(redisKey);

        log.info("[AUTH] 第3步调试: userId={}, redisKey={}, redisValue类型={}, redisValue={}",
                userId,
                redisKey,
                redisValue != null ? redisValue.getClass().getName() : "null",
                redisValue != null ? redisValue.toString().substring(0, Math.min(30, redisValue.toString().length())) + "..." : "null"
        );

        if (redisValue == null || !token.equals(redisValue.toString())) {
            log.warn("[AUTH] 失败-第3步: Redis中Token不匹配或不存在");
            writeError(response, ResultCode.UNAUTHORIZED);
            return false;
        }

        // 4. 放入线程上下文
        String role = jwtUtil.getRole(token);
        UserContext.setUserId(userId);
        UserContext.setRole(role);
        log.info("[AUTH] 验证通过: userId={}, role={}", userId, role);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }

    private void writeError(HttpServletResponse response, ResultCode resultCode) throws Exception {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(R.fail(resultCode)));
    }
}