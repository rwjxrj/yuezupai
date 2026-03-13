package com.yuezupai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("rent_demand")
public class RentDemand {

    @TableId(type = IdType.AUTO)
    private Long demandId;

    private Long userId;
    private String title;
    private String description;
    private String budgetDesc;
    private String expectTime;
    private String wechatId;
    private String phone;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}