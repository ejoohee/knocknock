package com.knocknock.domain.airInfo.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "airstation")
public class AirStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_id")
    private Integer stationId;

    // 지역명
    @Column(name = "station_region", nullable = false)
    private String stationRegion;

    // 지역상세
    @Column(name = "region_detail")
    private String regionDetail;

    // 측정소명
    @Column(name = "station_name") // 측정소명은 중복 허용 안됨
    private String stationName;

}
