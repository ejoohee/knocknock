package com.knocknock.domain.model.service;

import com.knocknock.domain.model.dto.request.AddMyModelReqDto;

public interface MyModelService {
    void addMyModel(AddMyModelReqDto addMyModelReqDto);

    void deleteMyModel(long modelId);

}
