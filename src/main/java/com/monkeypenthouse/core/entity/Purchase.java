package com.monkeypenthouse.core.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name="purchase")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name="order_id", nullable=false)
    private String orderId;

    @Column(name = "order_name", nullable = false)
    private String orderName;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @CreatedDate
    @Column(name="created_at", updatable=false, nullable=false)
    private LocalDateTime createdAt;

    public Purchase(User user, String orderId, String orderName, int amount, OrderStatus orderStatus) {
        this.user = user;
        this.orderId = orderId;
        this.orderName = orderName;
        this.amount = amount;
        this.orderStatus = orderStatus;
    }

    public void changeOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
