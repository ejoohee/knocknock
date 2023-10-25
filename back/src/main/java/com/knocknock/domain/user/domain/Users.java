package com.knocknock.domain.user.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
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

    @Column(unique = true, nullable = false)
    private String email; // 아이디

    @Column
    private String password;

    @Column(length = 20 )
    private String nickname;

    @Column(nullable = false, columnDefinition = "varchar(50)")
    @ColumnDefault("'ROLE_USER'")
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(length = 10)
    private Integer giroCode;

    @Column
    private String address;


    @Builder
    public Users(String email, String password, String nickname, Integer giroCode, String address, String userType) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.giroCode = giroCode;
        this.address = address;
        this.userType = UserType.getUserType(userType);
    }

    // 비밀번호 변경
    public void updatePassword(String encodingPassword) {
        this.password = encodingPassword;
    }

}
