package com.java.bmart.domain.delivery.service.request;


import org.springframework.data.domain.Pageable;

public record FindWaitingDeliveriesCommand(Pageable pageable) {

    public static FindWaitingDeliveriesCommand from(final Pageable pageable) {
        return new FindWaitingDeliveriesCommand(pageable);
    }
}
