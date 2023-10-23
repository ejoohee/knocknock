package com.knocknock.domain.model.domain;

import com.knocknock.domain.user.domain.Users;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "likemodel")
public class LikeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_model_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    Model model;

    @Builder
    public LikeModel(Users user, Model model) {
        this.user = user;
        this.model = model;
    }

}
