package com.java.bmart.domain.event.service.request;

public record FindEventDetailCommand(Long eventId) {

    public static FindEventDetailCommand from(final Long eventId) {
        return new FindEventDetailCommand(eventId);
    }
}
