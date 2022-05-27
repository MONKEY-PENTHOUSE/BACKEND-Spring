package com.monkeypenthouse.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @CreatedDate
    @Column(name="created_at", updatable=false, nullable=false)
    private LocalDateTime createdAt;

}
