package com.monkeypenthouse.core.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@Table(name="purchase_ticket_mapping")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "is_active = true")
@EntityListeners(AuditingEntityListener.class)
public class PurchaseTicketMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(nullable = false)
    private int quantity;

    @Column(name="is_active", nullable = false)
    @ColumnDefault("true")
    @Builder.Default
    private boolean isActive = true;

    public PurchaseTicketMapping(Purchase purchase, Ticket ticket, int quantity) {
        this.purchase = purchase;
        this.ticket = ticket;
        this.quantity = quantity;
    }
}
