package com.yuezupai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderCreateDTO {

    /** 物品ID */
    @NotNull(message = "物品ID不能为空")
    private Long itemId;

    /** 订单类型：direct-直租, reserve-预约, negotiate-议价 */
    @NotBlank(message = "订单类型不能为空")
    private String orderType;
}