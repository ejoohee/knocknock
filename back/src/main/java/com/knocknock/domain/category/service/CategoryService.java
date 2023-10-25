package com.knocknock.domain.category.service;

import com.knocknock.domain.category.dto.response.FindCategoryListResDto;

import java.util.List;

public interface CategoryService {
    List<FindCategoryListResDto> findCategoryList();

}
