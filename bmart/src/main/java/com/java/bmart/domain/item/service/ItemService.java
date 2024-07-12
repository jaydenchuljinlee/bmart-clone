package com.java.bmart.domain.item.service;

import com.java.bmart.domain.category.MainCategory;
import com.java.bmart.domain.category.SubCategory;
import com.java.bmart.domain.category.exception.NotFoundCategoryException;
import com.java.bmart.domain.category.repository.MainCategoryRepository;
import com.java.bmart.domain.category.repository.SubCategoryRepository;
import com.java.bmart.domain.item.Item;
import com.java.bmart.domain.item.exception.NotFoundItemException;
import com.java.bmart.domain.item.repository.ItemRepository;
import com.java.bmart.domain.item.service.request.*;
import com.java.bmart.domain.item.service.response.FindItemDetailResponse;
import com.java.bmart.domain.item.service.response.FindItemsResponse;
import com.java.bmart.domain.item.service.response.ItemRedisDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final MainCategoryRepository mainCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ItemCacheService itemCacheService;


    @Transactional(readOnly = true)
    public FindItemsResponse findItemByCategory(
            FindItemsByCategoryCommand findItemsByCategoryCommand
    ) {
        String subCategoryName = findItemsByCategoryCommand.subCategoryName();

        if (subCategoryName == null || subCategoryName.isBlank()) {
            return FindItemsResponse.from(findItemsByMainCategoryFrom(findItemsByCategoryCommand));
        }

        List<Item> items = findItemsBySubCategoryFrom(findItemsByCategoryCommand);
        return FindItemsResponse.from(items);
    }

    @Transactional(readOnly = true)
    public FindItemDetailResponse findItemDetail(FindItemDetailCommand findItemDetailCommand) {
        Item item = itemRepository.findById(findItemDetailCommand.itemId())
                .orElseThrow(() -> new NotFoundItemException("존재하지 않는 상품입니다."));

        return FindItemDetailResponse.of(
                item.getItemId(),
                item.getName(),
                item.getPrice(),
                item.getDescription(),
                item.getQuantity(),
                item.getRate(),
                item.getReviews().size(),
                item.getDiscount(),
                item.getLikeItems().size(),
                item.getMaxBuyQuantity()
        );
    }

    @Transactional(readOnly = true)
    public FindItemsResponse findNewItems(FindNewItemsCommand findNewItemsCommand) {
        return FindItemsResponse.from(
                itemRepository.findNewItemsOrderBy(findNewItemsCommand.lastIdx(),
                        findNewItemsCommand.lastItemId(), findNewItemsCommand.sortType(),
                        findNewItemsCommand.pageRequest()));
    }

    @Transactional
    public Long saveItem(RegisterItemCommand registerItemCommand) {
        Long mainCategoryId = registerItemCommand.mainCategoryId();
        Long subCategoryId = registerItemCommand.subCategoryId();

        MainCategory mainCategory = getMainCategoryById(mainCategoryId);
        SubCategory subCategory = getSubCategoryById(subCategoryId);

        Item item = Item.builder()
                .name(registerItemCommand.name())
                .price(registerItemCommand.price())
                .description(registerItemCommand.description())
                .quantity(registerItemCommand.quantity())
                .discount(registerItemCommand.discount())
                .maxBuyQuantity(registerItemCommand.maxBuyQuantity())
                .mainCategory(mainCategory)
                .subCategory(subCategory)
                .build();

        Item savedItem = itemRepository.save(item);
        itemCacheService.saveNewItem(ItemRedisDto.from(savedItem));
        return savedItem.getItemId();
    }

    @Transactional
    public void updateItem(UpdateItemCommand updateItemCommand) {
        Long itemId = updateItemCommand.itemId();
        Long mainCategoryId = updateItemCommand.mainCategoryId();
        Long subCategoryId = updateItemCommand.subCategoryId();

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundItemException("해당 아이템이 존재하지 않습니다."));

        MainCategory mainCategory = getMainCategoryById(mainCategoryId);
        SubCategory subCategory = getSubCategoryById(subCategoryId);

        item.updateItem(
                updateItemCommand.name(),
                updateItemCommand.price(),
                updateItemCommand.quantity(),
                updateItemCommand.description(),
                mainCategory,
                subCategory,
                updateItemCommand.discount()
        );
    }

    @Transactional
    public void deleteById(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    private SubCategory getSubCategoryById(Long subCategoryId) {
        SubCategory subCategory = null;
        if (subCategoryId != null) {
            subCategory = subCategoryRepository.findById(subCategoryId)
                    .orElseThrow(() -> new NotFoundCategoryException("없는 소카테고리입니다."));
        }
        return subCategory;
    }

    private MainCategory getMainCategoryById(Long mainCategoryId) {
        MainCategory mainCategory = null;
        if (mainCategoryId != null) {
            mainCategory = mainCategoryRepository.findById(mainCategoryId)
                    .orElseThrow(() -> new NotFoundCategoryException("없는 대카테고리입니다."));
        }
        return mainCategory;
    }

    private List<Item> findItemsByMainCategoryFrom(
            FindItemsByCategoryCommand findItemsByCategoryCommand) {

        Long lastItemId = findItemsByCategoryCommand.lastItemId();
        Long lastIdx = findItemsByCategoryCommand.lastIdx();
        ItemSortType itemSortType = findItemsByCategoryCommand.itemSortType();
        PageRequest pageRequest = findItemsByCategoryCommand.pageRequest();
        String mainCategoryName = findItemsByCategoryCommand.mainCategoryName().toLowerCase();
        MainCategory mainCategory = mainCategoryRepository.findByName(mainCategoryName)
                .orElseThrow(() -> new NotFoundCategoryException("없는 대카테고리입니다."));

        return itemRepository.findByMainCategoryOrderBy(mainCategory, lastIdx, lastItemId,
                itemSortType, pageRequest);
    }

    private List<Item> findItemsBySubCategoryFrom(
            FindItemsByCategoryCommand findItemsByCategoryCommand) {

        Long lastItemId = findItemsByCategoryCommand.lastItemId();
        Long lastIdx = findItemsByCategoryCommand.lastIdx();
        ItemSortType itemSortType = findItemsByCategoryCommand.itemSortType();
        PageRequest pageRequest = findItemsByCategoryCommand.pageRequest();
        String mainCategoryName = findItemsByCategoryCommand.mainCategoryName().toLowerCase();
        String subCategoryName = findItemsByCategoryCommand.subCategoryName().toLowerCase();
        MainCategory mainCategory = mainCategoryRepository.findByName(mainCategoryName)
                .orElseThrow(() -> new NotFoundCategoryException("없는 대카테고리입니다."));
        SubCategory subCategory = subCategoryRepository.findByName(subCategoryName)
                .orElseThrow(() -> new NotFoundCategoryException("없는 소카테고리입니다."));

        return itemRepository.findBySubCategoryOrderBy(mainCategory, subCategory, lastIdx,
                lastItemId, itemSortType, pageRequest);
    }
}
