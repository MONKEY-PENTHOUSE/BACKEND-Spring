package com.monkeypenthouse.core.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@IdClass(DibsId.class)
@Builder
@Table(name="dibs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dibs {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "amenity_id")
    private Amenity amenity;

}
