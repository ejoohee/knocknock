package com.knocknock.domain.model.service;

import com.knocknock.domain.model.dto.request.AddMyModelReqDto;
import com.knocknock.domain.model.dto.response.FindModelResDto;
import com.knocknock.domain.model.dto.response.FindMyModelListResDto;
import com.knocknock.domain.model.dto.response.FindMyModelResDto;

import java.util.List;

public interface MyModelService {
    void addMyModel(AddMyModelReqDto addMyModelReqDto);

    void deleteMyModel(long modelId);

    List<FindMyModelListResDto> findMyModelList(String category);

    FindMyModelResDto findMyModel(long myModelId);

    void pinMyModel(long myModelId);

}
