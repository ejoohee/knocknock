package com.knocknock.domain.category.api;

import com.knocknock.domain.category.dto.response.FindCategoryListResDto;
import com.knocknock.domain.category.service.CategoryService;
import com.knocknock.domain.model.dto.response.FindModelListResDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "가전제품 카테고리 목록 조회하기",
            description = "가전제품 카테고리 목록을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<List<FindCategoryListResDto>> findCategoryList() {
        return ResponseEntity.ok(categoryService.findCategoryList());
    }

}
