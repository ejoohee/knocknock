package com.knocknock.domain.model.dao;

import com.knocknock.domain.model.dto.response.FindMyModelListResDto;

import java.util.List;

public interface MyModelRepositoryCustom {

    List<FindMyModelListResDto> findMyModelList(Long userId, String category);

}
