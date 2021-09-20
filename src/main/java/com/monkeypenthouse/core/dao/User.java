package com.monkeypenthouse.core.dao;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=15, nullable=false)
    private String name;

    @Column(length=8, nullable=false)
    private String birth;

    @Column(nullable=false)
    private int gender;

    @Column(unique = true, length=50, nullable=false)
    private String email;

    @Column(nullable=false)
    private String password;

    @Column(name="phone_num", unique = true, length=20, nullable=false)
    private String phoneNum;

    @Column(name="collect_personal_info", nullable=false)
    private boolean canCollectPersonalInfo;

    @Column(name="receive_info", nullable=false)
    private boolean canReceiveInfo;

    @Column(name="room_num", unique = true, length=8, nullable=false)
    private String roomNum;
}
