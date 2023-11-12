package com.knocknock.domain.model.dao;

import com.knocknock.domain.category.domain.QCategory;
import com.knocknock.domain.model.domain.LikeModel;
import com.knocknock.domain.model.domain.QLikeModel;
import com.knocknock.domain.model.domain.QModel;
import com.knocknock.domain.model.dto.response.FindLikeModelListResDto;
import com.knocknock.domain.model.dto.response.FindModelListResDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LikeModelRepositoryCustomImpl implements LikeModelRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<FindLikeModelListResDto> findLikeModelList(long userId, String category) {
        QLikeModel qLikeModel = QLikeModel.likeModel;
        QCategory qCategory = QCategory.category;
        QModel qModel = QModel.model;
        BooleanBuilder categoryOption = new BooleanBuilder();
        // 어떤 카테고리 인지 (null 이면 전체 조회)
        if(category != null && !category.equals("")){
            categoryOption.and(
                    qModel.category.id.in(
                            queryFactory.select(qCategory.id)
                                    .from(qCategory)
                                    .where(qCategory.name.eq(category))
                    ));
        }
        List<LikeModel> likeModelList = queryFactory
                .select(qLikeModel)
                .from(qLikeModel)
                .join(qLikeModel.model)
                .fetchJoin()
                .where(
                        qLikeModel.user.userId.eq(userId),
                        categoryOption
                ).fetch();
        return likeModelList.stream().map((likeModel) -> FindLikeModelListResDto.builder()
                        .likeModelId(likeModel.getId())
                        .modelId(likeModel.getModel().getId())
                        .category(category)
                        .modelName(likeModel.getModel().getName())
                        .modelBrand(likeModel.getModel().getBrand())
                        .build()
                )
                .collect(Collectors.toList());
    }

}
