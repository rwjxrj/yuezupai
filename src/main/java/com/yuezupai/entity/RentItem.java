package com.yuezupai.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@TableName(value = "rent_item", autoResultMap = true)
public class RentItem {

    @TableId(type = IdType.AUTO)
    private Long itemId;

    private Long ownerId;
    private String type;
    private String name;
    private String description;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    private String location;
    private Integer status;
    private Integer allowReserve;

    // ★ 改为 Map 类型，MyBatis-Plus 自动 JSON 序列化/反序列化
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> billingRule;

    private BigDecimal priceNormal;
    private BigDecimal priceVip;
    private BigDecimal depositAmount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}