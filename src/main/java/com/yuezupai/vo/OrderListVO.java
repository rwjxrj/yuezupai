package com.yuezupai.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderListVO {

    private Long orderId;
    private String orderNo;
    private String orderType;
    private Integer status;

    /** 物品简要信息 */
    private Long itemId;
    private String itemName;
    private String itemCoverImage;

    /** 价格 */
    private BigDecimal originalPrice;
    private BigDecimal actualPrice;
    private BigDecimal depositAmount;

    /** 时间 */
    private LocalDateTime createTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}