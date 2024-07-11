package com.java.bmart.domain.payment;


import com.java.bmart.domain.order.Order;
import com.java.bmart.domain.user.User;
import com.java.bmart.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment")
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payId;

    @Setter
    private String paymentKey;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Builder
    public Payment(User user, Order order) {
        this.user = user;
        this.order = order;
    }

    public void changeStatus(final PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public boolean isMisMatchPrice(final int amount) {
        return this.order.isMisMatchPrice(amount);
    }

    public boolean isMisMatchStatus(final PaymentStatus paymentStatus) {
        return this.paymentStatus != paymentStatus;
    }
}
