package com.yuezupai.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 编辑物品请求参数（所有字段选填，传哪个改哪个）
 */
@Data
public class ItemUpdateDTO {

    private String name;
    private String description;
    private List<String> images;
    private String location;

    @DecimalMin(value = "0.00", message = "价格不能为负数")
    private BigDecimal priceNormal;

    @DecimalMin(value = "0.00", message = "VIP价格不能为负数")
    private BigDecimal priceVip;

    @DecimalMin(value = "0.00", message = "押金不能为负数")
    private BigDecimal depositAmount;

    private Integer allowReserve;
    private Map<String, Object> billingRule;
}