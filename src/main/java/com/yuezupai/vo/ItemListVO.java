package com.yuezupai.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 物品列表项（首页瀑布流用，只返回必要字段）
 */
@Data
public class ItemListVO {

    private Long itemId;

    /** 分类 */
    private String type;

    /** 物品名称 */
    private String name;

    /** 封面图（取images数组第一张） */
    private String coverImage;

    /** 取货地点 */
    private String location;

    /** 普通价 */
    private BigDecimal priceNormal;

    /** VIP价 */
    private BigDecimal priceVip;

    /** 状态：0-空闲 1-已预约 2-租用中 3-维护中 4-已下架 */
    private Integer status;
}