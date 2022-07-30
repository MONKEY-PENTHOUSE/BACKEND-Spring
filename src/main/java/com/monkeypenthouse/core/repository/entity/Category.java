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
@Table(name="category")
@Data
@Where(clause = "is_active = true")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=10, unique = true, nullable=false)
    private String name;

    @Column(name="is_active", nullable = false)
    @ColumnDefault("true")
    @Builder.Default
    private Boolean isActive = true;
}
