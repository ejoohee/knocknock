package com.knocknock.domain.model.domain;

import com.knocknock.domain.user.domain.Users;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mymodel")
public class MyModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_model_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    Model model;

    @Column(length = 50, name = "model_nickname")
    String modelNickname;

    @Column(name = "add_at_pin")
    Date addAtPin;

    @Builder
    public MyModel(Users user, Model model, String modelNickname, Date addAtPin) {
        this.user = user;
        this.model = model;
        this.modelNickname = modelNickname;
        this.addAtPin = addAtPin;
    }

}
