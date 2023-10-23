package com.knocknock.domain.model.service;

import com.knocknock.domain.model.dto.response.FindModelListResDto;
import com.knocknock.domain.model.dto.response.FindModelResDto;

import java.util.List;

public interface ModelService {

    List<FindModelListResDto> findModelList(String type, String keyword, String category);

    FindModelResDto findModel(long modelId);
}
