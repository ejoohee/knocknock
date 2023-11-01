package com.knocknock.domain.model.dao;

import com.knocknock.domain.category.domain.QCategory;
import com.knocknock.domain.model.constant.SearchType;
import com.knocknock.domain.model.domain.*;
import com.knocknock.domain.model.dto.response.CheckModelResDto;
import com.knocknock.domain.model.dto.response.FindModelListResDto;
import com.knocknock.domain.model.dto.response.FindMyModelListResDto;
import com.knocknock.domain.user.domain.QUsers;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;


/**
 * queryDSL용 Repository
 */
@RequiredArgsConstructor
public class ModelRepositoryCustomImpl implements ModelRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<FindModelListResDto> findModelList(Long userId, String type, String keyword, String category) {
        QModel qModel = QModel.model;
        QCategory qCategory = QCategory.category;
        QUsers qUsers = QUsers.users;
        QMyModel qMyModel = QMyModel.myModel;
        QLikeModel qLikeModel = QLikeModel.likeModel;

        BooleanBuilder searchOption = new BooleanBuilder();
        BooleanBuilder categoryOption = new BooleanBuilder();
        // 검색어가 null이 아닐 때
        if(keyword != null) {
            // 검색 유형이 뭔지
            if (type.equals(SearchType.BRAND.getValue())) {
                // 브랜드로 검색
                searchOption.and(qModel.brand.contains(keyword));
            }
            if (type.equals(SearchType.MODEL.getValue())) {
                // 모델명으로 검색
                searchOption.and(qModel.name.contains(keyword));
            }
        }
        // 어떤 카테고리 인지 (null 이면 전체 조회)
        if(category != null && !category.equals("")){
            categoryOption.and(
                    qModel.category.id.in(
                    queryFactory.select(qCategory.id)
                            .from(qCategory)
                            .where(qCategory.name.eq(category))
            ));
        }
        // 가전제품 목록 조회 시
        List<FindModelListResDto> modelDtoList = queryFactory
                .select(Projections.bean(FindModelListResDto.class,
                        qModel.id.as("modelId"), qModel.name.as("modelName"),
                        qModel.brand.as("modelBrand"), qModel.grade.as("modelGrade")))
                .from(qModel)
                .where(
                        // 내 가전에 포함된거 제외
                        qModel.id.notIn(
                                queryFactory.select(qMyModel.model.id)
                                        .from(qMyModel)
                                        .where(qMyModel.user.userId.eq(userId))
                        ), searchOption, categoryOption
                )
                // 에너지 효율 등급이 높은순으로 정렬
                .orderBy(qModel.grade.asc())
                .fetch();

        return modelDtoList;
    }

    @Override
    public CheckModelResDto checkModelByModelName(String modelName) {
        QModel qModel = QModel.model;
        QCategory qCategory = QCategory.category;
        CheckModelResDto checkModelResDto = queryFactory
                .select(Projections.bean(CheckModelResDto.class,
                        qModel.name.as("modelName"), qModel.brand.as("modelBrand")
                        , qModel.img.as("modelImg"), qCategory.name.as("category")))
                .from(qModel)
                .join(qModel.category, qCategory)
                .fetchJoin()
                .where(
                        // 모델명과 일치하는 가전제품인지
                        qModel.name.eq(modelName)
                )
                .fetchOne();

        return checkModelResDto;
    }

}
