package com.yuezupai.service;

import com.yuezupai.dto.OrderCreateDTO;

import java.util.Map;

public interface OrderService {

    /**
     * 创建订单
     * @param dto 创建参数
     * @return 包含 orderId, orderNo, status
     */
    Map<String, Object> create(OrderCreateDTO dto);
}