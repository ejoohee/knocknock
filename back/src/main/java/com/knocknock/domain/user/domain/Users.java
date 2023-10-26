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

    @Column(nullable = false)
    private String password;

    @Column(length = 20, nullable = false)
    private String nickname;

    @Column(nullable = false, columnDefinition = "varchar(50)")
    @ColumnDefault("'ROLE_USER'")
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(length = 10)
    private String giroCode;

    @Column(nullable = false)
    private String address;

    @ColumnDefault("false")
    private Boolean isSocial;

    @Builder
    public Users(String email, String password, String nickname, String giroCode, String address, String userType) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.giroCode = giroCode;
        this.address = address;
        this.userType = UserType.getUserType(userType);
    }

    /**
     * 패스워드 변경 서비스
     */
    public void updatePassword(String encodingPassword) {
        this.password = encodingPassword;
    }

    /**
     * 내 정보 수정 서비스
     */
    public void updateUser(String nickname, String address, String giroCode) {
        this.nickname = nickname == null ? this.nickname : nickname;
        this.address = address == null ? this.address : address;
        this.giroCode = giroCode == null ? this.giroCode : giroCode;
    }

    /**
     * 지로 코드 등록 서비스
     */
    public void addGiroCode(String giroCode) {
        this.giroCode = giroCode == null ? this.giroCode : giroCode;
    }

}
