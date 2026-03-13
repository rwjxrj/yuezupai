package com.yuezupai.ws;

import com.yuezupai.common.constant.Constants;
import com.yuezupai.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 聊天处理器（骨架，第3周完善业务逻辑）
 */
@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    /** 在线用户会话映射: userId → WebSocketSession */
    private static final Map<Long, WebSocketSession> ONLINE_SESSIONS = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(JwtUtil jwtUtil, RedisTemplate<String, Object> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    /** 连接建立 */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = getUserIdFromSession(session);
        if (userId == null) {
            try { session.close(); } catch (Exception ignored) {}
            return;
        }

        ONLINE_SESSIONS.put(userId, session);
        redisTemplate.opsForValue().set(Constants.REDIS_WS_ONLINE_PREFIX + userId, "1");
        log.info("WebSocket连接建立: userId={}", userId);
    }

    /** 收到消息 */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        Long userId = getUserIdFromSession(session);
        String payload = message.getPayload();
        log.info("收到消息: userId={}, msg={}", userId, payload);

        // TODO 第3周实现：解析JSON → 存库 → 转发给对方
    }

    /** 连接关闭 */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            ONLINE_SESSIONS.remove(userId);
            redisTemplate.delete(Constants.REDIS_WS_ONLINE_PREFIX + userId);
            log.info("WebSocket连接关闭: userId={}", userId);
        }
    }

    /** 从连接URL的token参数中提取userId */
    private Long getUserIdFromSession(WebSocketSession session) {
        try {
            URI uri = session.getUri();
            if (uri == null) return null;
            String token = UriComponentsBuilder.fromUri(uri).build()
                    .getQueryParams().getFirst("token");
            if (token == null || !jwtUtil.validateToken(token)) return null;
            return jwtUtil.getUserId(token);
        } catch (Exception e) {
            log.warn("WebSocket Token解析失败", e);
            return null;
        }
    }

    /** 给指定用户发消息（供Service层调用） */
    public void sendToUser(Long targetUserId, String message) {
        WebSocketSession session = ONLINE_SESSIONS.get(targetUserId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                log.error("WebSocket发送失败: targetUserId={}", targetUserId, e);
            }
        }
    }

    /** 判断用户是否在线 */
    public boolean isOnline(Long userId) {
        return ONLINE_SESSIONS.containsKey(userId);
    }
}