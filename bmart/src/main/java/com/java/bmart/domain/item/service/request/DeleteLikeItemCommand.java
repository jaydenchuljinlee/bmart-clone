package com.java.bmart.domain.item.service.request;

public record DeleteLikeItemCommand(Long userId, Long likeItemId) {

    public static DeleteLikeItemCommand of(final Long userId, final Long likeItemId) {
        return new DeleteLikeItemCommand(userId, likeItemId);
    }
}
