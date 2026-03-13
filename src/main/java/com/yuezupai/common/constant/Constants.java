package com.yuezupai.common.constant;

/**
 * 全局常量
 */
public class Constants {

    private Constants() {}

    // ==================== Redis Key 前缀 ====================
    /** 用户Token: token:{userId} → tokenString */
    public static final String REDIS_TOKEN_PREFIX = "token:";

    /** 物品锁（下单防并发）: lock:item:{itemId} */
    public static final String REDIS_ITEM_LOCK_PREFIX = "lock:item:";

    /** WebSocket在线状态: ws:online:{userId} */
    public static final String REDIS_WS_ONLINE_PREFIX = "ws:online:";

    /** 未读消息计数: unread:{userId} */
    public static final String REDIS_UNREAD_PREFIX = "unread:";

    // ==================== Token 过期时间 ====================
    /** Token在Redis中的过期时间（秒）- 7天 */
    public static final long TOKEN_EXPIRE_SECONDS = 7 * 24 * 60 * 60;

    // ==================== 物品状态 ====================
    public static final int ITEM_STATUS_FREE = 0;         // 空闲可租
    public static final int ITEM_STATUS_RESERVED = 1;     // 已被预约
    public static final int ITEM_STATUS_RENTING = 2;      // 租用中
    public static final int ITEM_STATUS_MAINTENANCE = 3;  // 维护中
    public static final int ITEM_STATUS_OFF_SHELF = 4;    // 已下架

    // ==================== 订单状态 ====================
    public static final int ORDER_STATUS_PENDING_PRICE = 10;    // 待改价
    public static final int ORDER_STATUS_PENDING_PAY = 20;      // 待支付
    public static final int ORDER_STATUS_RESERVED = 30;         // 已预约
    public static final int ORDER_STATUS_USING = 40;            // 使用中
    public static final int ORDER_STATUS_PENDING_FINISH = 50;   // 待结单
    public static final int ORDER_STATUS_FINISHED = 60;         // 已完成
    public static final int ORDER_STATUS_CANCELLED = 70;        // 已取消

    // ==================== 用户角色 ====================
    public static final String ROLE_USER = "user";
    public static final String ROLE_ADMIN = "admin";
}