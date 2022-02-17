package com.monkeypenthouse.core.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@IdClass(AmenityCategoryId.class)
@Builder
@Table(name="amenity_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmenityCategory {
    @Id
    @ManyToOne
    @JoinColumn(name = "amenity_id")
    private Amenity amenity;

    @Id
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
