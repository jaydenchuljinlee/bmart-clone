package com.java.bmart.domain.delivery.controller.request;

import com.java.bmart.domain.delivery.DeliveryStatus;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record FindRiderDeliveriesRequest(
    @NotEmpty(message = "배달 상태는 하나 이상 주어져야 합니다.")
    List<DeliveryStatus> deliveryStatuses) {

}
