package com.yuezupai.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 物品详情（点击进入详情页用，返回完整信息）
 */
@Data
public class ItemDetailVO {

    private Long itemId;
    private String type;
    private String name;
    private String description;
    private List<String> images;
    private String location;
    private Integer status;
    private Integer allowReserve;
    private Map<String, Object> billingRule;
    private BigDecimal priceNormal;
    private BigDecimal priceVip;
    private BigDecimal depositAmount;
    private LocalDateTime createTime;

    /** 物主信息（内嵌对象） */
    private OwnerInfo ownerInfo;

    @Data
    public static class OwnerInfo {
        private Long userId;
        private String nickname;
        private String avatarUrl;
    }
}