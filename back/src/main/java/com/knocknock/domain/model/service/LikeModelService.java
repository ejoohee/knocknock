package com.knocknock.domain.model.service;

import com.knocknock.domain.model.dto.response.FindLikeModelListResDto;

import java.util.List;

public interface LikeModelService {

    void addLikeModel(long modelId);

    void deleteLikeModel(long modelId);

    List<FindLikeModelListResDto> findLikeModelList(String category);

}
