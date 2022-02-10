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

    @Column(name="deadline_date", nullable=false)
    private LocalDate deadlineDate;

    @Column(name="start_date", nullable=false)
    private LocalDate startDate;

    @CreatedDate
    @Column(name="created_at", updatable=false, nullable=false)
    private LocalDateTime createdAt;

    @Column(name="detail", length=50, nullable=false)
    private String detail;

    @Column(name="thumbnail_name")
    private String thumbnailName;

    // 0 : 추천 X
    // 1 : 추천 O
    @Column(nullable = false)
    private int recommended;

    @Column(name="min_person_num", nullable = false)
    private int minPersonNum;

    @Column(name="max_person_num", nullable = false)
    private int maxPersonNum;

    // 0 : 모집중
    // 1 : 모집 마감
    // 2 : 종료
    @Column(nullable=false)
    private int status;

    @ManyToOne
    @JoinColumn(nullable=false)
    private Category category;

}
