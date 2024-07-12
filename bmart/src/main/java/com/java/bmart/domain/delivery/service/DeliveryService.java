package com.java.bmart.domain.delivery.service;

import com.java.bmart.domain.delivery.Delivery;
import com.java.bmart.domain.delivery.Rider;
import com.java.bmart.domain.delivery.exception.AlreadyRegisteredDeliveryException;
import com.java.bmart.domain.delivery.exception.NotFoundDeliveryException;
import com.java.bmart.domain.delivery.exception.NotFoundRiderException;
import com.java.bmart.domain.delivery.exception.UnauthorizedDeliveryException;
import com.java.bmart.domain.delivery.repository.DeliveryRepository;
import com.java.bmart.domain.delivery.repository.RiderRepository;
import com.java.bmart.domain.delivery.service.request.*;
import com.java.bmart.domain.delivery.service.response.FindDeliveryByOrderResponse;
import com.java.bmart.domain.delivery.service.response.FindDeliveryDetailResponse;
import com.java.bmart.domain.delivery.service.response.FindRiderDeliveriesResponse;
import com.java.bmart.domain.delivery.service.response.FindWaitingDeliveriesResponse;
import com.java.bmart.domain.notification.NotificationType;
import com.java.bmart.domain.notification.service.NotificationService;
import com.java.bmart.domain.notification.service.request.SendNotificationCommand;
import com.java.bmart.domain.order.Order;
import com.java.bmart.domain.order.exception.NotFoundOrderException;
import com.java.bmart.domain.order.repository.OrderRepository;
import com.java.bmart.domain.user.User;
import com.java.bmart.domain.user.exception.NotFoundUserException;
import com.java.bmart.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.java.bmart.domain.notification.NotificationMessage.COMPLETE_DELIVERY;
import static com.java.bmart.domain.notification.NotificationMessage.REGISTER_DELIVERY;

@RequiredArgsConstructor
@Service
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;
    private final RiderRepository riderRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public FindDeliveryByOrderResponse findDeliveryByOrder(
            FindDeliveryByOrderCommand findDeliveryByOrderCommand
    ) {
        User findUser = findUserByUserId(findDeliveryByOrderCommand.userId());
        Delivery findDelivery = findDeliveryByOrderWithOrder(findDeliveryByOrderCommand.orderId());

        checkAuthority(findDelivery, findUser);
        return FindDeliveryByOrderResponse.from(findDelivery);
    }

    @Transactional(readOnly = true)
    public FindDeliveryDetailResponse findDelivery(
            FindDeliveryDetailCommand findDeliveryDetailCommand
    ) {
        Delivery findDelivery = findDeliveryByDeliveryIdWithOrderAndOrderItems(findDeliveryDetailCommand);
        return FindDeliveryDetailResponse.from(findDelivery);
    }

    @Transactional(readOnly = true)
    public FindWaitingDeliveriesResponse findWaitingDeliveries(
            FindWaitingDeliveriesCommand findWaitingDeliveriesCommand
    ) {
        Page<Delivery> deliveriesPage = deliveryRepository.findWaitingDeliveries(findWaitingDeliveriesCommand.pageable());

        return FindWaitingDeliveriesResponse.from(deliveriesPage);
    }

    @Transactional(readOnly = true)
    public FindRiderDeliveriesResponse findRiderDeliveries(
            FindRiderDeliveriesCommand findRiderDeliveriesCommand
    ) {
        Rider findRider = findRiderByRiderId(findRiderDeliveriesCommand.riderId());
        Page<Delivery> deliveriesPage = deliveryRepository.findRiderDeliveries(findRider, findRiderDeliveriesCommand.deliveryStatuses(), findRiderDeliveriesCommand.pageable());

        return FindRiderDeliveriesResponse.of(
                deliveriesPage.getContent(),
                deliveriesPage.getNumber(),
                deliveriesPage.getTotalElements()
        );
    }

    @Transactional
    public Long registerDelivery(RegisterDeliveryCommand registerDeliveryCommand) {
        checkUserHasRegisterDeliveryAuthority(registerDeliveryCommand.userId());
        Order findOrder = findOrderByOrderIdPessimistic(registerDeliveryCommand.orderId());
        checkAlreadyRegisteredDelivery(findOrder);
        Delivery delivery = new Delivery(findOrder, registerDeliveryCommand.estimateMinutes());
        deliveryRepository.save(delivery);

        sendRegisterDeliveryNotification(registerDeliveryCommand, delivery, findOrder);

        return delivery.getDeliveryId();
    }

    @Transactional
    public void acceptDelivery(AcceptDeliveryCommand acceptDeliveryCommand) {
        Rider findRider = findRiderByRiderId(acceptDeliveryCommand.riderId());
        Delivery findDelivery = findDeliveryByDeliveryIdOptimistic(acceptDeliveryCommand);
        findDelivery.assignRider(findRider);
    }

    @Transactional
    public void startDelivery(StartDeliveryCommand startDeliveryCommand) {
        Rider findRider = findRiderByRiderId(startDeliveryCommand.riderId());
        Delivery findDelivery = findDeliveryByDeliveryId(startDeliveryCommand.deliveryId());
        findDelivery.checkAuthority(findRider);
        findDelivery.startDelivery(startDeliveryCommand.deliveryEstimateMinutes());

        sendCompleteDeliveryNotification(findDelivery);
    }

    private Delivery findDeliveryByDeliveryId(final Long deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NotFoundDeliveryException("존재하지 않는 배달입니다."));
    }

    private Rider findRiderByRiderId(final Long riderId) {
        return riderRepository.findById(riderId)
                .orElseThrow(() -> new NotFoundRiderException("존재하지 않는 라이더입니다."));
    }

    private Delivery findDeliveryByDeliveryIdWithOrderAndOrderItems(
            FindDeliveryDetailCommand findDeliveryDetailCommand) {
        return deliveryRepository.findByIdWithOrderAndItems(
                        findDeliveryDetailCommand.deliveryId())
                .orElseThrow(() -> new NotFoundDeliveryException("존재하지 않는 배달입니다."));
    }

    private User findUserByUserId(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("존재하지 않는 유저입니다."));
    }

    private Delivery findDeliveryByOrderWithOrder(final Long orderId) {
        return deliveryRepository.findByOrderIdWithOrder(orderId)
                .orElseThrow(() -> new NotFoundDeliveryException("존재하지 않는 배달입니다."));
    }

    private void checkAuthority(final Delivery delivery, final User user) {
        if (!delivery.isOwnByUser(user)) {
            throw new UnauthorizedDeliveryException("권한이 없습니다.");
        }
    }

    private void checkUserHasRegisterDeliveryAuthority(final Long userId) {
        User user = findUserByUserId(userId);
        if (!user.isEmployee()) {
            throw new UnauthorizedDeliveryException("권한이 없습니다.");
        }
    }

    private Order findOrderByOrderIdPessimistic(Long orderId) {
        return orderRepository.findByIdPessimistic(orderId)
                .orElseThrow(() -> new NotFoundOrderException("존재하지 않는 주문입니다."));
    }

    private Delivery findDeliveryByDeliveryIdOptimistic(
            AcceptDeliveryCommand acceptDeliveryCommand) {
        return deliveryRepository.findByIdOptimistic(acceptDeliveryCommand.deliveryId())
                .orElseThrow(() -> new NotFoundDeliveryException("존재하지 않는 배달입니다."));
    }

    private void checkAlreadyRegisteredDelivery(final Order order) {
        if (deliveryRepository.existsByOrder(order)) {
            throw new AlreadyRegisteredDeliveryException("이미 배달이 생성된 주문입니다.");
        }
    }

    private void sendRegisterDeliveryNotification(
            RegisterDeliveryCommand registerDeliveryCommand,
            Delivery delivery,
            Order order) {
        SendNotificationCommand notificationCommand = SendNotificationCommand.of(
                delivery.getUserId(),
                REGISTER_DELIVERY.getTitle(),
                REGISTER_DELIVERY.getContentFromFormat(
                        order.getName(),
                        registerDeliveryCommand.estimateMinutes()),
                NotificationType.DELIVERY);
        notificationService.sendNotification(notificationCommand);
    }

    private void sendCompleteDeliveryNotification(Delivery delivery) {
        SendNotificationCommand notificationCommand = SendNotificationCommand.of(
                delivery.getUserId(),
                COMPLETE_DELIVERY.getTitle(),
                COMPLETE_DELIVERY.getContentFromFormat(),
                NotificationType.DELIVERY);
        notificationService.sendNotification(notificationCommand);
    }
}
