package com.java.bmart.domain.item.service.response;

import com.java.bmart.domain.item.Item;
import com.java.bmart.domain.item.LikeItem;
import org.springframework.data.domain.Page;

import java.util.List;

public record FindLikeItemsResponse(
    List<FindLikeItemResponse> items,
    int page,
    long totalElements) {

    public static FindLikeItemsResponse from(final Page<LikeItem> likeItemPage) {
        Page<FindLikeItemResponse> findLikeItemResponsePage
            = likeItemPage.map(FindLikeItemResponse::from);
        return new FindLikeItemsResponse(
            findLikeItemResponsePage.getContent(),
            findLikeItemResponsePage.getNumber(),
            findLikeItemResponsePage.getTotalElements());
    }

    public record FindLikeItemResponse(
        Long likeItemId,
        Long itemId,
        String name,
        int price,
        int discount,
        int reviewCount,
        int like,
        double rate) {

        public static FindLikeItemResponse from(final LikeItem likeItem) {
            Item item = likeItem.getItem();
            return new FindLikeItemResponse(
                likeItem.getLikeItemId(),
                item.getItemId(),
                item.getName(),
                item.getPrice(),
                item.getDiscount(),
                item.getReviews().size(),
                item.getLikeItems().size(),
                item.getRate());
        }
    }
}
