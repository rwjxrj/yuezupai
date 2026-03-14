package com.yuezupai.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailVO {

    private Long orderId;
    private String orderNo;
    private String orderType;
    private Integer status;

    private BigDecimal originalPrice;
    private BigDecimal actualPrice;
    private BigDecimal depositAmount;

    private LocalDateTime reserveExpireTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;

    private List<String> pickUpImages;
    private List<String> returnImages;
    private String cancelReason;
    private String remark;

    /** 物品信息 */
    private ItemInfo itemInfo;

    /** 对方用户信息（租客看到物主，物主看到租客） */
    private UserInfo otherUserInfo;

    @Data
    public static class ItemInfo {
        private Long itemId;
        private String name;
        private String type;
        private List<String> images;
        private String location;
    }

    @Data
    public static class UserInfo {
        private Long userId;
        private String nickname;
        private String avatarUrl;
        private String phone;
    }
}