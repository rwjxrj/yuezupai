package com.yuezupai.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "rent_review", autoResultMap = true)
public class RentReview {

    @TableId(type = IdType.AUTO)
    private Long reviewId;

    private Long orderId;
    private Long itemId;
    private Long userId;
    private Integer rating;
    private String content;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}