package com.knocknock.domain.model.service;

import com.knocknock.domain.model.dto.response.FindModelListReqDto;

import java.util.List;

public interface ModelService {

    List<FindModelListReqDto> findModelList(String type, String keyword, String category);


}
