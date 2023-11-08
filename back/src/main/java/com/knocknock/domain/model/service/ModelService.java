package com.knocknock.domain.model.service;

import com.knocknock.domain.model.dto.request.CheckModelByLabelImgReqDto;
import com.knocknock.domain.model.dto.response.CheckModelResDto;
import com.knocknock.domain.model.dto.response.CompareModelAndMyModelResDto;
import com.knocknock.domain.model.dto.response.FindModelListResDto;
import com.knocknock.domain.model.dto.response.FindModelResDto;

import java.util.List;

public interface ModelService {

    List<FindModelListResDto> findModelList(String type, String keyword, String category);

    FindModelResDto findModel(long modelId);

    CheckModelResDto checkModelByModelName(String modelName);

    CompareModelAndMyModelResDto compareModelAndMyModel(long modelId, long myModelId);

    CheckModelResDto checkModelByLabelImg(CheckModelByLabelImgReqDto checkModelByLabelImgReqDto);

}
