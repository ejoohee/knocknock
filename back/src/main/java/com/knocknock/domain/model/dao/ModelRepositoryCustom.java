package com.knocknock.domain.model.dao;

import com.knocknock.domain.model.dto.response.CheckModelResDto;
import com.knocknock.domain.model.dto.response.FindModelListResDto;

import java.util.List;

public interface ModelRepositoryCustom {

    List<FindModelListResDto> findModelList(Long userId, String type, String keyword, String category);

    CheckModelResDto checkModelByModelName(String modelName);

}
