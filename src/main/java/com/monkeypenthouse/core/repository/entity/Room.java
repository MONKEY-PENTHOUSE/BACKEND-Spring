package com.monkeypenthouse.core.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Builder
@Table(name="room")
@Data
@Where(clause = "is_active = true")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Room {

    @Id
    @Column(length=8, updatable=false)
    private String id;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "user_role", nullable = false, updatable = false)
    private Authority authority;

    @Column(name = "user_id", unique = true)
    private Long userId;

    @Column(name="is_active", nullable = false)
    @ColumnDefault("true")
    @Builder.Default
    private boolean isActive = true;
}
