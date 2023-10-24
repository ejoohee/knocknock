package com.knocknock.domain.model.dao;

import com.knocknock.domain.model.domain.MyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MyModelRepository extends JpaRepository<MyModel, Long>, MyModelRepositoryCustom {

    // userId와 modelId로 회원이 삭제하려는 가전제품을 찾아 삭제
    @Modifying
    @Query(value = "DELETE FROM MyModel mm WHERE mm.user.userId = :userId AND mm.model.id = :modelId")
    void deleteByUserAndModel(long userId, long modelId);

    // 내가 등록한 가전제품 목록 조회
    @Query(value = "SELECT mm FROM MyModel mm WHERE mm.user.userId = :userId")
    List<MyModel> findAllByUser(long userId);

}
