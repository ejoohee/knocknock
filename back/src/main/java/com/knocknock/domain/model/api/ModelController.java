package com.knocknock.domain.model.api;

import com.knocknock.domain.model.dto.response.FindModelListReqDto;
import com.knocknock.domain.model.service.ModelService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<FindModelListReqDto>> findModelList(@RequestParam("type")String type, @RequestParam("keyword")String keyword, @RequestParam("category")String category) {
        return ResponseEntity.ok(modelService.findModelList(type, keyword, category));
    }

}
