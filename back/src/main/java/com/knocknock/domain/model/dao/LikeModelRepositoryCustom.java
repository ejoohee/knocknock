package com.knocknock.domain.model.dao;

import com.knocknock.domain.model.dto.response.FindLikeModelListResDto;

import java.util.List;

public interface LikeModelRepositoryCustom {

    List<FindLikeModelListResDto> findLikeModelList(long userId, String category);

}
