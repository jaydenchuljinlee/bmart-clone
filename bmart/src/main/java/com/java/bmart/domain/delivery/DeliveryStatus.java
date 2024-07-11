package com.java.bmart.domain.delivery;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DeliveryStatus {
    ACCEPTING_ORDER,
    START_DELIVERY,
    DELIVERED;
}