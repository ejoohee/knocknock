package com.knocknock.domain.user.dao;

import com.knocknock.domain.user.domain.CityCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CityCodeRepository extends JpaRepository<CityCode, Long> {

    @Query(value = "SELECT cc FROM CityCode cc JOIN FETCH cc.metroCode WHERE cc.metroCode.metroName = :metroName AND cc.cityName = :cityName")
    Optional<CityCode> findByMetroNameAndCityName(String metroName, String cityName);

}
