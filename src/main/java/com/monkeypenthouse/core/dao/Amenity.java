package com.monkeypenthouse.core.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@Table(name="amenity")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=30, unique = true, nullable=false)
    private String title;

    @Column(nullable=false)
    private String address;

    @CreatedDate
    @Column(name="registered_at", updatable=false, nullable=false)
    private LocalDateTime registeredDateTime;

    @LastModifiedDate
    @Column(name="last_modified_at")
    private LocalDateTime lastModifiedDateTime;

    @Column(name="deadline_date_time", nullable=false)
    private LocalDateTime deadlineDateTime;

    @Column(name="event_date_time", nullable=false)
    private LocalDateTime eventDateTime;

    @Column(name="achieve_rate", nullable=false)
    private int achieveRate;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable=false)
    private Category category;

    @Column(name="detail", length=50, nullable=false)
    private String detail;

    @Column( nullable=false)
    private boolean achieved;

    @Column(name="thumbnail_name")
    private String thumbnailName;
}
