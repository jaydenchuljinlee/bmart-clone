package com.java.bmart.domain.order.service;

import com.java.bmart.domain.coupon.Coupon;
import com.java.bmart.domain.coupon.UserCoupon;
import com.java.bmart.domain.coupon.exception.InvalidCouponException;
import com.java.bmart.domain.coupon.exception.NotFoundUserCouponException;
import com.java.bmart.domain.coupon.repository.UserCouponRepository;
import com.java.bmart.domain.item.Item;
import com.java.bmart.domain.item.exception.InvalidItemException;
import com.java.bmart.domain.item.exception.NotFoundItemException;
import com.java.bmart.domain.item.repository.ItemRepository;
import com.java.bmart.domain.order.Order;
import com.java.bmart.domain.order.OrderItem;
import com.java.bmart.domain.order.OrderStatus;
import com.java.bmart.domain.order.controller.request.CreateOrderRequest;
import com.java.bmart.domain.order.exception.NotFoundOrderException;
import com.java.bmart.domain.order.repository.OrderRepository;
import com.java.bmart.domain.order.service.request.CreateOrdersCommand;
import com.java.bmart.domain.order.service.request.UpdateOrderByCouponCommand;
import com.java.bmart.domain.order.service.response.CreateOrderResponse;
import com.java.bmart.domain.order.service.response.FindOrderDetailResponse;
import com.java.bmart.domain.order.service.response.FindOrdersResponse;
import com.java.bmart.domain.order.service.response.UpdateOrderByCouponResponse;
import com.java.bmart.domain.user.User;
import com.java.bmart.domain.user.exception.NotFoundUserException;
import com.java.bmart.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {
    private static final Integer PAGE_SIZE = 10;

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserCouponRepository userCouponRepository;

    @Transactional(readOnly = true)
    public FindOrderDetailResponse findOrderByIdAndUserId(final Long userId, final Long orderId) {
        final Order order = getOrderByOrderIdAndUserId(orderId, userId);
        return FindOrderDetailResponse.from(order);
    }

    @Transactional(readOnly = true)
    public FindOrdersResponse findOrders(final Long userId, final Integer page) {
        final Page<Order> pagination = orderRepository.findByUser_UserId(userId, PageRequest.of(page, PAGE_SIZE));

        return FindOrdersResponse.of(pagination.getContent(), pagination.getTotalPages());
    }

    @Transactional
    public CreateOrderResponse createOrder(final CreateOrdersCommand createOrdersCommand) {
        User findUser = findUserByUserId(createOrdersCommand.userId());
        List<OrderItem> orderItems = createOrderItem(createOrdersCommand.createOrderRequest().createOrderItemRequests());

        Order order = new Order(findUser, orderItems);
        orderRepository.save(order).getOrderId();

        return CreateOrderResponse.from(order);
    }

    @Transactional
    public UpdateOrderByCouponResponse updateOrderByCoupon(
            final UpdateOrderByCouponCommand updateOrderByCouponCommand) {
        Order findOrder = getOrderByOrderIdAndUserId(updateOrderByCouponCommand.orderId(), updateOrderByCouponCommand.userId());

        UserCoupon findUserCoupon = findUserCouponByIdWithCoupon(updateOrderByCouponCommand.couponId());
        validationCoupon(findOrder, findUserCoupon.getCoupon());
        findOrder.setUserCoupon(findUserCoupon);

        return UpdateOrderByCouponResponse.of(findOrder, findUserCoupon.getCoupon());
    }

    @Transactional
    public void updateOrderStatus() {
        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(30);
        List<OrderStatus> statusList = List.of(OrderStatus.PENDING, OrderStatus.PAYING);

        List<Order> expiredOrders = orderRepository.findByStatusInBeforeExpiredTime(expiredTime, statusList);

        for (Order expiredOrder: expiredOrders) {
            updateItemQuantity(expiredOrder);
            expiredOrder.updateOrderStatus(OrderStatus.CANCELED);
        }
    }

    @Transactional
    public void cancelOrder(final Order order) {
        order.updateOrderStatus(OrderStatus.CANCELED);
        order.unUseCoupon();
        order.getOrderItems().forEach(orderItem ->
                itemRepository.increaseQuantity(orderItem.getItem().getItemId(), orderItem.getQuantity()));
    }

    @Transactional
    public void deleteOrder(final Long orderId, final Long userId) {
        Order order = getOrderByOrderIdAndUserId(orderId, userId);
        orderRepository.delete(order);
    }

    public List<OrderItem> createOrderItem(final List<CreateOrderRequest.CreateOrderItemRequest> orderItemRequests) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (CreateOrderRequest.CreateOrderItemRequest createOrderRequest: orderItemRequests) {
            Item findItem = findItemByItemId(createOrderRequest.itemId());
            Integer quantity = createOrderRequest.quantity();
            validateItemQuantity(findItem, quantity);
            findItem.decreaseQuantity(quantity);

            OrderItem orderItem = new OrderItem(findItem, quantity);
            orderItems.add(orderItem);
        }

        return orderItems;
    }

    private static void updateItemQuantity(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            orderItem.getItem().increaseQuantity(orderItem.getQuantity());
        }
    }

    public Order getOrderByOrderIdAndUserId(final Long orderId, final Long userId) {
        return orderRepository.findByOrderIdAndUser_UserId(orderId, userId)
                .orElseThrow(() -> new NotFoundOrderException("order 가 존재하지 않습니다"));
    }

    private User findUserByUserId(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("존재하지 않은 사용자입니다."));
    }

    private Item findItemByItemId(final Long itemId) {
        return itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new NotFoundItemException("존재하지 않는 상품입니다."));
    }

    private UserCoupon findUserCouponByIdWithCoupon(Long UserCouponId) {
        return userCouponRepository.findByIdWithCoupon(UserCouponId)
                .orElseThrow(() -> new NotFoundUserCouponException("존재하지 않는 쿠폰입니다"));
    }

    private void validateItemQuantity(final Item findItem, final Integer quantity) {
        if (findItem.getQuantity() - quantity < 0) {
            throw new InvalidItemException("상품의 재고 수량이 부족합니다");
        }
    }

    private void validationCoupon(Order order, Coupon coupon) {
        if (order.getPrice() < coupon.getMinOrderPrice()) {
            throw new InvalidCouponException("총 주문 금액이 쿠폰 최소 사용 금액보다 작습니다");
        }
    }
}
