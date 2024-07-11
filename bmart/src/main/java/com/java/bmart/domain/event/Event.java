package com.java.bmart.domain.event;

import com.java.bmart.domain.event.exception.InvalidEventDescriptionException;
import com.java.bmart.domain.event.exception.InvalidEventTitleException;
import com.java.bmart.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE)
    private List<EventItem> eventItemList = new ArrayList<>();

    public Event(String title, String description) {
        validateTitle(title);
        validateDescription(description);
        this.title = title;
        this.description = description;
    }

    private void validateTitle(String title) {
        if (isNull(title)) {
            throw new InvalidEventTitleException("이벤트 제목이 존재하지 않습니다.");
        }
    }

    private void validateDescription(String description) {
        if (isNull(description)) {
            throw new InvalidEventDescriptionException("이벤트 설명이 존재하지 않습니다.");
        }
    }
}
