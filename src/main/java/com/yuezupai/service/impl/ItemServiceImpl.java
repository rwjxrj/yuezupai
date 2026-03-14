package com.yuezupai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuezupai.common.constant.Constants;
import com.yuezupai.common.exception.BusinessException;
import com.yuezupai.common.result.ResultCode;
import com.yuezupai.dto.ItemPublishDTO;
import com.yuezupai.dto.ItemUpdateDTO;
import com.yuezupai.entity.RentItem;
import com.yuezupai.entity.SysUser;
import com.yuezupai.mapper.RentItemMapper;
import com.yuezupai.mapper.SysUserMapper;
import com.yuezupai.service.ItemService;
import com.yuezupai.util.UserContext;
import com.yuezupai.vo.ItemDetailVO;
import com.yuezupai.vo.ItemListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final RentItemMapper itemMapper;
    private final SysUserMapper userMapper;

    private static final Set<String> VALID_TYPES = Set.of("vehicle", "digital", "daily", "idle");

    public ItemServiceImpl(RentItemMapper itemMapper, SysUserMapper userMapper) {
        this.itemMapper = itemMapper;
        this.userMapper = userMapper;
    }

    // ==================== 发布物品 ====================
    @Override
    public Long publish(ItemPublishDTO dto) {
        Long userId = UserContext.getUserId();
        String role = UserContext.getRole();

        if (!VALID_TYPES.contains(dto.getType())) {
            throw new BusinessException("物品类型不合法，允许值：vehicle / digital / daily / idle");
        }

        if (!"idle".equals(dto.getType()) && !Constants.ROLE_ADMIN.equals(role)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "仅管理员可发布车辆/数码/日用类物品");
        }

        RentItem item = new RentItem();
        item.setOwnerId(userId);
        item.setType(dto.getType());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setImages(dto.getImages());
        item.setLocation(dto.getLocation());
        item.setStatus(Constants.ITEM_STATUS_FREE);
        item.setAllowReserve(dto.getAllowReserve() != null ? dto.getAllowReserve() : 0);
        item.setBillingRule(dto.getBillingRule());
        item.setPriceNormal(dto.getPriceNormal());
        item.setPriceVip(dto.getPriceVip() != null ? dto.getPriceVip() : dto.getPriceNormal());
        item.setDepositAmount(dto.getDepositAmount());

        itemMapper.insert(item);

        log.info("物品发布成功: itemId={}, type={}, name={}, ownerId={}",
                item.getItemId(), item.getType(), item.getName(), userId);

        return item.getItemId();
    }

    // ==================== 物品列表 ====================
    @Override
    public IPage<ItemListVO> list(String type, String keyword, Integer page, Integer size) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;
        if (size > 50) size = 50;

        LambdaQueryWrapper<RentItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(RentItem::getStatus, Constants.ITEM_STATUS_OFF_SHELF);

        if (type != null && !type.isBlank()) {
            if (!VALID_TYPES.contains(type)) {
                throw new BusinessException("物品类型不合法");
            }
            wrapper.eq(RentItem::getType, type);
        }

        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(RentItem::getName, keyword.trim());
        }

        wrapper.orderByDesc(RentItem::getCreateTime);

        Page<RentItem> pageParam = new Page<>(page, size);
        IPage<RentItem> itemPage = itemMapper.selectPage(pageParam, wrapper);

        return itemPage.convert(this::toListVO);
    }

    // ==================== 物品详情 ====================
    @Override
    public ItemDetailVO detail(Long itemId) {
        // 1. 查物品
        RentItem item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "物品不存在");
        }

        // 已下架的物品不允许查看
        if (item.getStatus() == Constants.ITEM_STATUS_OFF_SHELF) {
            throw new BusinessException(ResultCode.NOT_FOUND, "该物品已下架");
        }

        // 2. 查物主信息
        SysUser owner = userMapper.selectById(item.getOwnerId());

        // 3. 组装VO
        ItemDetailVO vo = new ItemDetailVO();
        vo.setItemId(item.getItemId());
        vo.setType(item.getType());
        vo.setName(item.getName());
        vo.setDescription(item.getDescription());
        vo.setImages(item.getImages());
        vo.setLocation(item.getLocation());
        vo.setStatus(item.getStatus());
        vo.setAllowReserve(item.getAllowReserve());
        vo.setBillingRule(item.getBillingRule());
        vo.setPriceNormal(item.getPriceNormal());
        vo.setPriceVip(item.getPriceVip());
        vo.setDepositAmount(item.getDepositAmount());
        vo.setCreateTime(item.getCreateTime());

        // 物主信息
        if (owner != null) {
            ItemDetailVO.OwnerInfo ownerInfo = new ItemDetailVO.OwnerInfo();
            ownerInfo.setUserId(owner.getUserId());
            ownerInfo.setNickname(owner.getNickname());
            ownerInfo.setAvatarUrl(owner.getAvatarUrl());
            vo.setOwnerInfo(ownerInfo);
        }

        log.info("查询物品详情: itemId={}", itemId);
        return vo;
    }

    @Override
    public void update(Long itemId, ItemUpdateDTO dto) {
        Long userId = UserContext.getUserId();
        String role = UserContext.getRole();

        RentItem item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "物品不存在");
        }

        // 权限：只有物主本人或admin可以编辑
        if (!item.getOwnerId().equals(userId) && !Constants.ROLE_ADMIN.equals(role)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权编辑该物品");
        }

        // 只更新传了的字段
        if (dto.getName() != null) item.setName(dto.getName());
        if (dto.getDescription() != null) item.setDescription(dto.getDescription());
        if (dto.getImages() != null) item.setImages(dto.getImages());
        if (dto.getLocation() != null) item.setLocation(dto.getLocation());
        if (dto.getPriceNormal() != null) item.setPriceNormal(dto.getPriceNormal());
        if (dto.getPriceVip() != null) item.setPriceVip(dto.getPriceVip());
        if (dto.getDepositAmount() != null) item.setDepositAmount(dto.getDepositAmount());
        if (dto.getAllowReserve() != null) item.setAllowReserve(dto.getAllowReserve());
        if (dto.getBillingRule() != null) item.setBillingRule(dto.getBillingRule());

        itemMapper.updateById(item);
        log.info("物品编辑成功: itemId={}, operator={}", itemId, userId);
    }

    @Override
    public void toggleShelf(Long itemId, String action) {
        Long userId = UserContext.getUserId();
        String role = UserContext.getRole();

        RentItem item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "物品不存在");
        }

        if (!item.getOwnerId().equals(userId) && !Constants.ROLE_ADMIN.equals(role)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权操作该物品");
        }

        if ("off".equals(action)) {
            // 下架：租用中的物品不能下架
            if (item.getStatus() == Constants.ITEM_STATUS_RENTING) {
                throw new BusinessException("该物品正在租用中，无法下架");
            }
            if (item.getStatus() == Constants.ITEM_STATUS_RESERVED) {
                throw new BusinessException("该物品已被预约，无法下架");
            }
            item.setStatus(Constants.ITEM_STATUS_OFF_SHELF);
        } else if ("on".equals(action)) {
            // 上架：只有已下架的才能重新上架
            if (item.getStatus() != Constants.ITEM_STATUS_OFF_SHELF) {
                throw new BusinessException("该物品未处于下架状态");
            }
            item.setStatus(Constants.ITEM_STATUS_FREE);
        } else {
            throw new BusinessException("action参数不合法，允许值：on / off");
        }

        itemMapper.updateById(item);
        log.info("物品{}成功: itemId={}, operator={}", "on".equals(action) ? "上架" : "下架", itemId, userId);
    }

    // ==================== 私有方法 ====================
    private ItemListVO toListVO(RentItem item) {
        ItemListVO vo = new ItemListVO();
        vo.setItemId(item.getItemId());
        vo.setType(item.getType());
        vo.setName(item.getName());
        vo.setLocation(item.getLocation());
        vo.setPriceNormal(item.getPriceNormal());
        vo.setPriceVip(item.getPriceVip());
        vo.setStatus(item.getStatus());

        List<String> images = item.getImages();
        if (images != null && !images.isEmpty()) {
            vo.setCoverImage(images.get(0));
        }

        return vo;
    }
}