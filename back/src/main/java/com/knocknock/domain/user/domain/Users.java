package com.knocknock.domain.user.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    private String email; // 아이디

    private String nickname;

    private String password;

    private UserType userType;

    private Integer giroCode;

    private String address;

    private Boolean isSocial;

    @Builder
    public Users(String email, String password, String nickname, Integer giroCode, String address, String userType) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.giroCode = giroCode;
        this.address = address;
        this.userType = UserType.getUserType(userType);
    }

}
