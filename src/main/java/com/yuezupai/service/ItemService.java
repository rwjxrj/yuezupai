package com.yuezupai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuezupai.dto.ItemPublishDTO;
import com.yuezupai.dto.ItemUpdateDTO;
import com.yuezupai.vo.ItemDetailVO;
import com.yuezupai.vo.ItemListVO;

public interface ItemService {

    /** 发布物品 */
    Long publish(ItemPublishDTO dto);

    /** 分页查询物品列表 */
    IPage<ItemListVO> list(String type, String keyword, Integer page, Integer size);

    /** 获取物品详情 */
    ItemDetailVO detail(Long itemId);
    /** 编辑物品 */
    void update(Long itemId, ItemUpdateDTO dto);

    /** 上下架物品 */
    void toggleShelf(Long itemId, String action);
}