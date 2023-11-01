package com.knocknock.domain.category.service;

import com.knocknock.domain.category.dao.CategoryRepository;
import com.knocknock.domain.category.dto.response.FindCategoryListResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<FindCategoryListResDto> findCategoryList() {
        log.info("[가전제품 카테고리 목록 조회] 가전제품 카테고리 목록 조회 요청.");

        return categoryRepository.findAllProjectedBy().stream().map(
                (category) -> FindCategoryListResDto.builder()
                        .categoryId(category.getId())
                        .categoryName(category.getName())
                        .build()
        ).collect(Collectors.toList());
    }



}
