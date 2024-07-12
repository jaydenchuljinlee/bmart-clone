package com.java.bmart.domain.review.service;

import com.java.bmart.domain.item.Item;
import com.java.bmart.domain.item.exception.NotFoundItemException;
import com.java.bmart.domain.item.repository.ItemRepository;
import com.java.bmart.domain.review.Review;
import com.java.bmart.domain.review.exception.NotFoundReviewException;
import com.java.bmart.domain.review.repository.ReviewRepository;
import com.java.bmart.domain.review.service.request.RegisterReviewCommand;
import com.java.bmart.domain.review.service.request.UpdateReviewCommand;
import com.java.bmart.domain.review.service.response.FindReviewsByItemResponse;
import com.java.bmart.domain.review.service.response.FindReviewsByUserResponse;
import com.java.bmart.domain.user.User;
import com.java.bmart.domain.user.exception.NotFoundUserException;
import com.java.bmart.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ReviewCacheService reviewCacheService;

    private static final String REVIEW_COUNT_CACHE_KEY = "reviewCount:Item:";
    private static final String AVERAGE_RATE_CACHE_KEY = "averageRating:Item:";

    @Transactional(readOnly = true)
    public FindReviewsByUserResponse findReviewsByUser(final Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("사용자를 찾을 수 없습니다"));

        List<Review> findReviews = reviewRepository.findAllByUserOrderByCreatedAt(findUser);

        return FindReviewsByUserResponse.of(findUser, findReviews);
    }

    @Transactional(readOnly = true)
    public FindReviewsByItemResponse findReviewsByItem(final Long itemId) {
        Item findItem = findItemByItemId(itemId);

        List<Review> findReviews = reviewRepository.findAllByItemOrderByCreatedAt(findItem);

        return FindReviewsByItemResponse.of(findItem.getItemId(), findReviews);
    }

    @Transactional(readOnly = true)
    public Long findTotalReviewsByItem(final Long itemId) {
        Item findItem = findItemByItemId(itemId);

        String cacheKey = REVIEW_COUNT_CACHE_KEY + findItem.getItemId();

        return reviewCacheService.getTotalNumberOfReviewsByItemId(findItem.getItemId(), cacheKey);
    }

    @Transactional(readOnly = true)
    public Double findAverageRatingByItem(final Long itemId) {
        Item findItem = findItemByItemId(itemId);

        String cacheKey = AVERAGE_RATE_CACHE_KEY + findItem.getItemId();
        return reviewCacheService.getAverageRatingByItemId(itemId, cacheKey);
    }

    @Transactional
    public Long registerReview(RegisterReviewCommand registerReviewCommand) {
        User findUser = userRepository.findById(registerReviewCommand.userId())
                .orElseThrow(() -> new NotFoundUserException("존재하지 않는 사용자입니다."));

        Item findItem = findItemByItemId(registerReviewCommand.itemId());

        Review review = Review.builder()
                .user(findUser)
                .item(findItem)
                .rate(registerReviewCommand.rate())
                .content(registerReviewCommand.content())
                .build();

        Review savedReview = reviewRepository.save(review);

        String cacheKey = REVIEW_COUNT_CACHE_KEY + findItem.getItemId();

        reviewCacheService.plusOneToTotalNumberOfReviewsByItemId(findItem.getItemId(), cacheKey);
        reviewCacheService.updateAverageRatingByItemId(findItem.getItemId(), cacheKey, savedReview.getRate());

        return savedReview.getReviewId();
    }

    @Transactional
    public void updateReview(UpdateReviewCommand updateReviewCommand) {
        Review findReview = reviewRepository.findById(updateReviewCommand.reviewId())
                .orElseThrow(() -> new NotFoundReviewException("리뷰를 찾을 수 없습니다"));

        findReview.changeRageAndContent(updateReviewCommand.rate(), updateReviewCommand.content());
    }

    @Transactional
    public void deleteReview(final Long reviewId) {
        Review findReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundReviewException("리뷰를 찾을 수 없습니다"));

        reviewRepository.delete(findReview);

        String cacheKey = REVIEW_COUNT_CACHE_KEY + findReview.getItem().getItemId();
        reviewCacheService.minusOneToTotalNumberOfReviewsByItemId(findReview.getItem().getItemId(), cacheKey);
    }

    private Item findItemByItemId(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundItemException("해당 상품을 찾을 수 없습니다."));
    }
}
