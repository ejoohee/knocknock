package com.knocknock.domain.model.domain;

import com.knocknock.domain.user.domain.Users;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_model_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "user_id")
    Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "model_id")
    Model model;

}
