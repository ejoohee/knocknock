package com.knocknock.domain.model.dao;

import com.knocknock.domain.model.dto.response.FindModelListReqDto;

import java.util.List;

public interface ModelRepositoryCustom {

    List<FindModelListReqDto> findModelList(Long userId, String type, String keyword, String category);

}
