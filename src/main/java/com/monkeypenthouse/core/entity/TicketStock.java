package com.monkeypenthouse.core.entity;

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
    private String ticketId;

    @Column(unique = true, nullable=false)
    private Integer totalQuantity;

    @Column(unique = true, nullable=false)
    private Integer purchasedQuantity;

    public void reduce(int quantity) {
        purchasedQuantity -= quantity;
    }
}
