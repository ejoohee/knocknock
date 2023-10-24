package com.knocknock.domain.model.dao;

import com.knocknock.domain.category.domain.QCategory;
import com.knocknock.domain.model.domain.MyModel;
import com.knocknock.domain.model.domain.QModel;
import com.knocknock.domain.model.domain.QMyModel;
import com.knocknock.domain.model.dto.response.FindMyModelListResDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MyModelRepositoryCustomImpl implements MyModelRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<FindMyModelListResDto> findMyModelList(Long userId, String category) {
        QModel qModel = QModel.model;
        QCategory qCategory = QCategory.category;
        QMyModel qMyModel = QMyModel.myModel;

        BooleanBuilder categoryOption = new BooleanBuilder();
        // 어떤 카테고리 인지 (null 이면 전체 조회)
        if(category != null){
            categoryOption.and(
                    qMyModel.model.category.id.in(
                            queryFactory.select(qCategory.id)
                                    .from(qCategory)
                                    .where(qCategory.name.eq(category))
                    ));
        }
        // 가전제품 목록 조회 시
        List<MyModel> myModelList = queryFactory
                .select(qMyModel)
                .from(qMyModel)
                .join(qMyModel.model, qModel)
                .fetchJoin()
                .join(qModel.category, qCategory)
                .fetchJoin()
                .where(
                        qMyModel.user.userId.eq(userId)
                        , categoryOption
                )
                // 핀한 내 가전제품이 핀한 순서대로 먼저 조회됨
                .orderBy(qMyModel.addAtPin.asc().nullsLast())
                .fetch();

        return myModelList.stream().map((myModel) ->
            FindMyModelListResDto.builder()
                    .myModelId(myModel.getId())
                    .modelId(myModel.getModel().getId())
                    .category(myModel.getModel().getCategory().getName())
                    .modelBrand(myModel.getModel().getBrand())
                    .modelGrade(myModel.getModel().getGrade())
                    .modelNickname(myModel.getModelNickname())
                    .addAtPin(myModel.getAddAtPin())
                    .build()
        ).collect(Collectors.toList());
    }

}
