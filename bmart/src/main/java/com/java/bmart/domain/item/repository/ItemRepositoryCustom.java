package com.java.bmart.domain.item.repository;

import com.java.bmart.domain.category.MainCategory;
import com.java.bmart.domain.category.SubCategory;
import com.java.bmart.domain.item.Item;
import com.java.bmart.domain.item.service.ItemSortType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepositoryCustom {

    List<Item> findNewItemsOrderBy(Long lastIdx, Long lastItemId, ItemSortType sortType,
                                   Pageable pageable);

    List<Item> findHotItemsOrderBy(Long lastIdx, Long lastItemId, ItemSortType sortType,
                                   Pageable pageable);

    List<Item> findByMainCategoryOrderBy(MainCategory mainCategory, Long lastIdx, Long lastItemId,
                                         ItemSortType sortType, Pageable pageable);

    List<Item> findBySubCategoryOrderBy(MainCategory mainCategory, SubCategory subCategory,
                                        Long lastIdx, Long lastItemId, ItemSortType sortType, Pageable pageable);
}
