package com.knocknock.domain.category.service;

import com.knocknock.domain.category.dao.CategoryRepository;
import com.knocknock.domain.category.dto.response.FindCategoryListResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<FindCategoryListResDto> findCategoryList() {
        return categoryRepository.findAllProjectedBy().stream().map(
                (category) -> FindCategoryListResDto.builder()
                        .categoryId(category.getId())
                        .categoryName(category.getName())
                        .build()
        ).collect(Collectors.toList());
    }

}
