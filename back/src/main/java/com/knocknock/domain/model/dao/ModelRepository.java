package com.knocknock.domain.model.dao;

import com.knocknock.domain.model.domain.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ModelRepository extends JpaRepository<Model, Long>, ModelRepositoryCustom {

    // 카테고리와 조인하여 가전제품 조회
    @Query(value = "SELECT m FROM Model m JOIN FETCH Category c WHERE m.id = :modelId")
    Optional<Model> findModelById(long modelId);

    // 모델명으로 가전제품 조회
    Optional<Model> findModelByName(String modelName);

}
