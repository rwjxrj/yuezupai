package com.yuezupai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuezupai.common.constant.Constants;
import com.yuezupai.common.exception.BusinessException;
import com.yuezupai.common.result.ResultCode;
import com.yuezupai.dto.OrderCreateDTO;
import com.yuezupai.entity.RentItem;
import com.yuezupai.entity.RentOrder;
import com.yuezupai.mapper.RentItemMapper;
import com.yuezupai.mapper.RentOrderMapper;
import com.yuezupai.service.OrderService;
import com.yuezupai.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final RentOrderMapper orderMapper;
    private final RentItemMapper itemMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final Set<String> VALID_ORDER_TYPES = Set.of("direct", "reserve", "negotiate");

    public OrderServiceImpl(RentOrderMapper orderMapper, RentItemMapper itemMapper,
                            RedisTemplate<String, Object> redisTemplate) {
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> create(OrderCreateDTO dto) {
        Long userId = UserContext.getUserId();

        // 1. 校验订单类型
        if (!VALID_ORDER_TYPES.contains(dto.getOrderType())) {
            throw new BusinessException("订单类型不合法，允许值：direct / reserve / negotiate");
        }

        // 2. Redis分布式锁，防止同一物品被同时下单
        String lockKey = Constants.REDIS_ITEM_LOCK_PREFIX + dto.getItemId();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, userId.toString(), 10, TimeUnit.SECONDS);
        if (locked == null || !locked) {
            throw new BusinessException("该物品正在被他人操作，请稍后重试");
        }

        try {
            return doCreate(dto, userId);
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * 实际创建逻辑（在锁内执行）
     */
    private Map<String, Object> doCreate(OrderCreateDTO dto, Long userId) {
        // 3. 查物品
        RentItem item = itemMapper.selectById(dto.getItemId());
        if (item == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "物品不存在");
        }

        // 4. 不能租自己的物品
        if (item.getOwnerId().equals(userId)) {
            throw new BusinessException("不能租赁自己的物品");
        }

        // 5. 根据订单类型校验物品状态
        String orderType = dto.getOrderType();

        if ("direct".equals(orderType) || "reserve".equals(orderType)) {
            // 直租和预约：物品必须是空闲状态
            if (item.getStatus() != Constants.ITEM_STATUS_FREE) {
                throw new BusinessException("该物品当前不可租用");
            }
            // 预约还需检查物品是否允许预约
            if ("reserve".equals(orderType) && item.getAllowReserve() != 1) {
                throw new BusinessException("该物品不支持预约，请直接租用");
            }
        } else {
            // negotiate议价：物品必须是闲置类型
            if (!"idle".equals(item.getType())) {
                throw new BusinessException("仅闲置类物品支持议价");
            }
            if (item.getStatus() != Constants.ITEM_STATUS_FREE) {
                throw new BusinessException("该物品当前不可租用");
            }
        }

        // 6. 检查用户是否已有该物品的进行中订单（防重复下单）
        Long existCount = orderMapper.selectCount(
                new LambdaQueryWrapper<RentOrder>()
                        .eq(RentOrder::getUserId, userId)
                        .eq(RentOrder::getItemId, dto.getItemId())
                        .notIn(RentOrder::getStatus,
                                Constants.ORDER_STATUS_FINISHED,
                                Constants.ORDER_STATUS_CANCELLED)
        );
        if (existCount > 0) {
            throw new BusinessException("你已有该物品的进行中订单");
        }

        // 7. 构建订单
        RentOrder order = new RentOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setOwnerId(item.getOwnerId());
        order.setItemId(item.getItemId());
        order.setOrderType(orderType);
        order.setOriginalPrice(item.getPriceNormal());
        order.setActualPrice(item.getPriceNormal());
        order.setDepositAmount(item.getDepositAmount());

        // 8. 根据类型设置不同初始状态
        switch (orderType) {
            case "direct":
                // 直租 → 待支付
                order.setStatus(Constants.ORDER_STATUS_PENDING_PAY);
                // 锁定物品
                item.setStatus(Constants.ITEM_STATUS_RESERVED);
                itemMapper.updateById(item);
                break;

            case "reserve":
                // 预约 → 待支付
                order.setStatus(Constants.ORDER_STATUS_PENDING_PAY);
                // 锁定物品
                item.setStatus(Constants.ITEM_STATUS_RESERVED);
                itemMapper.updateById(item);
                break;

            case "negotiate":
                // 议价 → 待改价（物品状态暂不锁定）
                order.setStatus(Constants.ORDER_STATUS_PENDING_PRICE);
                break;
        }

        // 9. 入库
        orderMapper.insert(order);

        log.info("订单创建成功: orderNo={}, type={}, itemId={}, userId={}, status={}",
                order.getOrderNo(), orderType, item.getItemId(), userId, order.getStatus());

        // 10. 组装返回
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getOrderId());
        result.put("orderNo", order.getOrderNo());
        result.put("status", order.getStatus());
        result.put("depositAmount", order.getDepositAmount());
        // 模拟支付：告诉前端当前是模拟模式
        result.put("payMode", "simulate");
        return result;
    }

    /**
     * 生成订单号: YZP + yyyyMMddHHmmss + 4位随机数
     */
    private String generateOrderNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "YZP" + time + random;
    }
}