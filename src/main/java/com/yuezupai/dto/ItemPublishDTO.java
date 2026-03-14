package com.yuezupai.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 发布物品 请求参数
 */
@Data
public class ItemPublishDTO {

    /** 物品分类：vehicle/digital/daily/idle */
    @NotBlank(message = "物品分类不能为空")
    private String type;

    /** 物品名称 */
    @NotBlank(message = "物品名称不能为空")
    private String name;

    /** 描述（选填） */
    private String description;

    /** 图片URL数组（至少1张） */
    @NotEmpty(message = "至少上传一张图片")
    private List<String> images;

    /** 取货地点（选填，如：A栋车棚） */
    private String location;

    /** 普通价格（元/时） */
    @NotNull(message = "普通价格不能为空")
    @DecimalMin(value = "0.00", message = "价格不能为负数")
    private BigDecimal priceNormal;

    /** VIP价格（选填，不填则等于普通价） */
    @DecimalMin(value = "0.00", message = "VIP价格不能为负数")
    private BigDecimal priceVip;

    /** 押金额度（0表示免押金） */
    @NotNull(message = "押金不能为空")
    @DecimalMin(value = "0.00", message = "押金不能为负数")
    private BigDecimal depositAmount;

    /** 是否允许预约：0-否 1-是（默认0） */
    private Integer allowReserve;

    /**
     * 计费规则（JSON对象，选填）
     * 示例：{"base_price": 5, "base_unit": 1, "max_per_day": 30}
     */
    private Map<String, Object> billingRule;
}