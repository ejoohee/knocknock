package com.knocknock.domain.model.dao;

import com.knocknock.domain.model.domain.LikeModel;
import com.knocknock.domain.model.domain.Model;
import com.knocknock.domain.user.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LikeModelRepository extends JpaRepository<LikeModel, Long> {

    // 찜한 상품인지 확인용
    @Query(value = "SELECT lm FROM LikeModel lm WHERE lm.user.userId = :userId AND lm.model.id = :modelId")
    Optional<LikeModel> findLikeModelByUserAndModel(long userId, long modelId);

    @Modifying
    @Query(value = "DELETE FROM LikeModel lm WHERE lm.user.userId = :userId AND lm.model.id = :modelId")
    void deleteByUserAndModel(long userId, long modelId);


}
