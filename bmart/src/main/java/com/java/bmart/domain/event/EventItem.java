package com.java.bmart.domain.event;


import com.java.bmart.domain.event.exception.NotFoundEventException;
import com.java.bmart.domain.item.Item;
import com.java.bmart.domain.item.exception.NotFoundItemException;
import com.java.bmart.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static java.util.Objects.isNull;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {"event_id", "item_id"}
        )
})
public class EventItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public EventItem(Event event, Item item) {
        validateEvent(event);
        validateItem(item);
        this.event = event;
        this.item = item;
    }

    public void validateEvent(Event event) {
        if (isNull(event)) {
            throw new NotFoundEventException("Event가 존재하지 않습니다.");
        }
    }

    private void validateItem(Item item) {
        if (isNull(item)) {
            throw new NotFoundItemException("Item이 존재하지 않습니다.");
        }
    }
}
