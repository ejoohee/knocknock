package com.knocknock.domain.model.domain;

import com.knocknock.domain.category.domain.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    @Column(name = "model_name", nullable = false)
    String name;

    @Column(name = "model_brand", nullable = false)
    String brand;

    @Column(name = "model_grade", nullable = false)
    Integer grade;

    @Column(name = "model_img", nullable = false)
    String img;

    @Column(name = "model_URL", nullable = false)
    String url;

    @Column(name = "model_usage")
    Integer usage;

    @Column(name = "model_usage2")
    Integer usage2;

    @Column(name = "model_usage3")
    Integer usage3;

    @Column(name = "model_co2", nullable = false)
    Integer co2;

    @Column(name = "model_cost", nullable = false)
    Integer cost;

    @Column(name = "like_count")
    @ColumnDefault("0")
    Integer likeCount;

    @Column(name = "released_date")
    Date releasedDate;

    // 비즈니스 메서드

    // 찜 개수 증가
    void increaseLikeCount(){
        this.likeCount++;
    }

    // 찜 개수 감소
    void decreaseLikeCount(){
        this.likeCount--;
    }


}
