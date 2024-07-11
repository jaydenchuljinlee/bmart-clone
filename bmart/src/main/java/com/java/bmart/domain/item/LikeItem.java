package com.java.bmart.domain.item;

import com.java.bmart.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "like_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    public LikeItem(User user, Item item) {
        this.user = user;
        this.item = item;
    }

    public boolean isSameUser(final Long userId) {
        return user.isSameUserId(userId);
    }
}

