package com.knocknock.domain.category.domain;

import javax.persistence.*;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name =  "category_id")
    Long id;

    @Column(length = 40, name = "category_name", nullable = false)
    String categoryName;

    @Column(length = 40, name = "usage")
    String usage;

    @Column(length = 40, name = "usage_unit")
    String usageUnit;

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

}
