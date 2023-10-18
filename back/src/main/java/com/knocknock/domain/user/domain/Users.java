package com.knocknock.domain.user.domain;

import javax.persistence.*;

@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;

    private String email; // 아이디

    private String userName;

    private String password;

    private UserType userType;

    private Integer giroCode;

    private Boolean isSocial;

}
