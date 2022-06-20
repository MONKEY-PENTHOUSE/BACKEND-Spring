package com.monkeypenthouse.core.repository.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@Table(name="ticket_stock")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class TicketStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable=false)
    private Long ticketId;

    @Column(nullable=false)
    private Long totalQuantity;

    @Column(nullable=false)
    private Long purchasedQuantity;

    public void increasePurchasedQuantity(Long quantity) {
        purchasedQuantity += quantity;
    }
}
