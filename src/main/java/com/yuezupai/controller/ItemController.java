package com.yuezupai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuezupai.common.result.R;
import com.yuezupai.dto.ItemPublishDTO;
import com.yuezupai.dto.ItemUpdateDTO;
import com.yuezupai.service.ItemService;
import com.yuezupai.vo.ItemDetailVO;
import com.yuezupai.vo.ItemListVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/item")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * 发布物品
     * POST /v1/item/publish
     */
    @PostMapping("/publish")
    public R<Map<String, Long>> publish(@Valid @RequestBody ItemPublishDTO dto) {
        Long itemId = itemService.publish(dto);
        return R.ok(Map.of("itemId", itemId));
    }

    /**
     * 分页查询物品列表（首页瀑布流）
     * GET /v1/item/list?type=vehicle&keyword=电动车&page=1&size=10
     */
    @GetMapping("/list")
    public R<IPage<ItemListVO>> list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        return R.ok(itemService.list(type, keyword, page, size));
    }

    /**
     * 获取物品详情（扫码/点击进入）
     * GET /v1/item/detail/1
     */
    @GetMapping("/detail/{itemId}")
    public R<ItemDetailVO> detail(@PathVariable Long itemId) {
        return R.ok(itemService.detail(itemId));
    }

    /**
     * 编辑物品
     * PUT /v1/item/update/1
     */
    @PutMapping("/update/{itemId}")
    public R<Void> update(@PathVariable Long itemId, @Valid @RequestBody ItemUpdateDTO dto) {
        itemService.update(itemId, dto);
        return R.ok();
    }

    /**
     * 上下架物品
     * POST /v1/item/toggle-shelf
     * Body: { "itemId": 1, "action": "off" }
     */
    @PostMapping("/toggle-shelf")
    public R<Void> toggleShelf(@RequestBody Map<String, Object> params) {
        Long itemId = Long.valueOf(params.get("itemId").toString());
        String action = params.get("action").toString();
        itemService.toggleShelf(itemId, action);
        return R.ok();
    }
}