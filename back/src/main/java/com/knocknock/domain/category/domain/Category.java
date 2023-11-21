package com.knocknock.domain.category.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name =  "category_id")
    Long id;

    @Column(length = 40, name = "category_name", nullable = false)
    String name;

    @Column(length = 40, name = "usage1")
    String usage1;

    @Column(length = 40, name = "usage_unit1")
    String usageUnit1;

    @Column(length = 40, name = "usage2")
    String usage2;

    @Column(length = 40, name = "usage_unit2")
    String usageUnit2;

    @Column(length = 40, name = "usage3")
    String usage3;

    @Column(length = 40, name = "usage_unit3")
    String usageUnit3;

    // 에너지 비용 단위
    @Column(length = 40, name = "cost_unit", nullable = false)
    String costUnit;

    // 에너지 비용 단위
    @Column(length = 40, name = "co2_unit", nullable = false)
    String co2Unit;


    @Builder
    public Category(String name, String usage1, String usageUnit1, String usage2, String usageUnit2, String usage3, String usageUnit3, String costUnit, String co2Unit) {
        this.name = name;
        this.usage1 = usage1;
        this.usageUnit1 = usageUnit1;
        this.usage2 = usage2;
        this.usageUnit2 = usageUnit2;
        this.usage3 = usage3;
        this.usageUnit3 = usageUnit3;
        this.costUnit = costUnit;
        this.co2Unit = co2Unit;
    }

}
