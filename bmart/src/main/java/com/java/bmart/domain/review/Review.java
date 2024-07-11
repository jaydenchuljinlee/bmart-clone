package com.java.bmart.domain.review;


import com.java.bmart.domain.item.Item;
import com.java.bmart.domain.item.exception.NotFoundItemException;
import com.java.bmart.domain.review.exception.InvalidReviewException;
import com.java.bmart.domain.user.User;
import com.java.bmart.domain.user.exception.NotFoundUserException;
import com.java.bmart.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static java.util.Objects.isNull;

@Getter
@Entity
@Table(name = "review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    public static final int MAX_CONTENT = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column
    private double rate;

    @Column
    private String content;

    @Builder
    public Review(User user, Item item, double rate, String content) {
        validateUser(user);
        validateItem(item);
        validateRate(rate);
        validateContent(content);

        this.user = user;
        this.item = item;
        this.rate = rate;
        this.content = content;
    }

    public void validateUser(
            final User user
    ) {
        if (isNull(user)) {
            throw new NotFoundUserException("User 가 존재하지 않습니다.");
        }
    }

    public void validateItem(
            final Item item
    ) {
        if (isNull(item)) {
            throw new NotFoundItemException("Item 이 존재하지 않습니다.");
        }
    }

    public void validateRate(
            double rate
    ) {
        if (0 > rate) {
            throw new InvalidReviewException("평점은 양수여야 합니다.");
        }

        if (rate > 5) {
            rate = 5;
        }
    }

    public void validateContent(
            final String content
    ) {
        if (content.isBlank()) {
            throw new InvalidReviewException("리뷰 내용은 1자 이상이어야 합니다.");
        }

        if (content.length() > MAX_CONTENT) {
            throw new InvalidReviewException("리뷰 내용은 100자를 넘을 수 없습니다.");
        }
    }

    public void changeRageAndContent(
            final double rate,
            final String content
    ) {
        validateRate(rate);
        validateContent(content);

        this.rate = rate;
        this.content = content;
    }
}
