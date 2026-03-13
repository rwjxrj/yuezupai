package com.yuezupai.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "rent_order", autoResultMap = true)
public class RentOrder {

    @TableId(type = IdType.AUTO)
    private Long orderId;

    private String orderNo;
    private Long userId;
    private Long ownerId;
    private Long itemId;
    private String orderType;
    private Integer status;
    private BigDecimal originalPrice;
    private BigDecimal actualPrice;
    private BigDecimal depositAmount;
    private String wechatScoreOrderNo;
    private String wechatPayOrderNo;
    private LocalDateTime reserveExpireTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> pickUpImages;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> returnImages;

    private String cancelReason;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}