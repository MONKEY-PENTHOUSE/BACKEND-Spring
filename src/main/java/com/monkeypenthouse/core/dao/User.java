package com.monkeypenthouse.core.dao;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Table(name="user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=15, nullable=false)
    private String name;

    @CreatedDate
    @Column(name="created_at", updatable=false)
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    @Column(name="last_modified_at", updatable=true)
    private LocalDateTime lastModifiedDateTime;

    @Column(nullable=false)
    private LocalDateTime birth;

    // 0 : 여자
    // 1: 남자
    @Column(nullable=false)
    private int gender;

    @Column(unique = true, length=50, nullable=false)
    private String email;

    @Column(nullable=false)
    private String password;

    @Column(name="phone_num", unique = true, length=20, nullable=false)
    private String phoneNum;

    @Column(name="collect_personal_info", nullable=false)
    private int canCollectPersonalInfo;

    @Column(name="receive_info", nullable=false)
    private int canReceiveInfo;

    @Column(name="room_num", unique = true, length=8, nullable=false)
    private String roomNum;

    @ElementCollection(fetch=FetchType.LAZY)
    @Builder.Default
    private Set<UserRole> roleSet = new HashSet<>();

    @Enumerated(EnumType.ORDINAL)
    @Column(name="login_type", nullable = false)
    private LoginType loginType;

    public void addUserRole(UserRole userRole) {
        roleSet.add(userRole);
    }
}
