package com.knocknock.domain.waste.dao;

import com.knocknock.domain.waste.domain.Waste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WasteRepository extends JpaRepository<Waste,Integer> {

}
