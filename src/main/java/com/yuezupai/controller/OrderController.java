package com.yuezupai.controller;

import com.yuezupai.common.result.R;
import com.yuezupai.dto.OrderCreateDTO;
import com.yuezupai.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 创建订单
     * POST /v1/order/create
     */
    @PostMapping("/create")
    public R<Map<String, Object>> create(@Valid @RequestBody OrderCreateDTO dto) {
        return R.ok(orderService.create(dto));
    }
}