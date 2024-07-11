package com.java.bmart.domain.order;


import com.java.bmart.domain.item.Item;
import com.java.bmart.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long OrderItemId;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public OrderItem(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public int calculateSubtotal() {
        return item.getPrice() * quantity;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}