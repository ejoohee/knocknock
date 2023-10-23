package com.knocknock.domain.model.api;

import com.knocknock.domain.model.dto.response.FindModelListResDto;
import com.knocknock.domain.model.dto.response.FindModelResDto;
import com.knocknock.domain.model.service.ModelService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/model")
public class ModelController {

    private final ModelService modelService;

    @Operation(
            summary = "가전제품 목록 조회하기",
            description = "가전제품의 목록을 검색 또는 카테고리 별 조회합니다. 내 가전에 등록된 제품은 제외하고 조회합니다."
    )
    @GetMapping
    public ResponseEntity<List<FindModelListResDto>> findModelList(@RequestParam("type")String type, @RequestParam("keyword")String keyword, @RequestParam("category")String category) {
        return ResponseEntity.ok(modelService.findModelList(type, keyword, category));
    }

    @Operation(
            summary = "가전제품 상세 정보 조회하기",
            description = "가전제품 상세 정보를 조회합니다."
    )
    @GetMapping("/{modelId}")
    public ResponseEntity<FindModelResDto> findMode(@PathVariable long modelId) {
        return ResponseEntity.ok(modelService.findModel(modelId));
    }

}
