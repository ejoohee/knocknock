package com.knocknock.domain.user.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "citycode")
public class CityCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_code_id")
    private Long cityCodeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metro_code")
    private MetroCode metroCode;

    @Column(name = "city_code")
    private Integer cityCode;

    @Column(name = "city_name")
    private String cityName;

}
