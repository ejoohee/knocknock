package com.knocknock.domain.model.domain;

import com.knocknock.domain.category.domain.Category;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Model")
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

    @Column(name = "model_URL")
    String url;

    @Column(name = "usage_value1")
    Float usageValue1;

    @Column(name = "usage_value2")
    Float usageValue2;

    @Column(name = "usage_value3")
    Float usageValue3;

    @Column(name = "model_co2", nullable = false)
    Integer co2;

    @Column(name = "model_cost", nullable = false)
    Integer cost;

    @Column(name = "like_count")
    @ColumnDefault("0")
    Integer likeCount;

    @Column(name = "released_date")
    Date releasedDate;

    @Builder
    public Model(Category category, String name, String brand, Integer grade, String img, String url, Float usageValue1, Float usageValue2, Float usageValue3, Integer co2, Integer cost, Date releasedDate) {
        this.category = category;
        this.name = name;
        this.brand = brand;
        this.grade = grade;
        this.img = img;
        this.url = url;
        this.usageValue1 = usageValue1;
        this.usageValue2 = usageValue2;
        this.usageValue3 = usageValue3;
        this.co2 = co2;
        this.cost = cost;
        this.releasedDate = releasedDate;
    }

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
