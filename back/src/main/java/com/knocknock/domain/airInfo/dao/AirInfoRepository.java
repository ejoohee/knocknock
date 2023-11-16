package com.knocknock.domain.airInfo.dao;

import com.knocknock.domain.airInfo.domain.AirStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AirInfoRepository extends JpaRepository<AirStation, Integer> {

    // 지역명으로 측정소 리스트 반환
    List<AirStation> findByStationRegionContaining(String region);

    // 특정 특정소 이름으로 특정소 반환
    Optional<AirStation> findByStationName(String name);

    // 전체 지역리스트 반환

    // region_detail로 측정소찾아
    Optional<AirStation> findByRegionDetailContaining(String regionDetail);

    @Query(value = "select * from airstation where station_region = :region limit 1", nativeQuery = true)
    AirStation selectOneByRegion(String region);
}
