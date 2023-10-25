package com.knocknock.domain.model.api;

import com.knocknock.domain.model.dto.request.AddMyModelReqDto;
import com.knocknock.domain.model.dto.response.*;
import com.knocknock.domain.model.service.LikeModelService;
import com.knocknock.domain.model.service.ModelService;
import com.knocknock.domain.model.service.MyModelService;
import com.knocknock.global.dto.MessageDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/model")
public class ModelController {

    private final ModelService modelService;
    private final MyModelService myModelService;
    private final LikeModelService likeModelService;

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
    public ResponseEntity<FindModelResDto> findModel(@PathVariable long modelId) {
        return ResponseEntity.ok(modelService.findModel(modelId));
    }

    @Operation(
            summary = "가전제품 모델명 확인용 조회하기",
            description = "가전제품 모델명으로 해당하는 모델이 존재하는지 정보를 조회합니다."
    )
    @GetMapping("/check")
    public ResponseEntity<CheckModelResDto> checkModelByModelName(@RequestParam("modelName")String modelName) {
        return ResponseEntity.ok(modelService.checkModelByModelName(modelName));
    }


    @Operation(
            summary = "내 가전제품 등록하기",
            description = "내 가전제품을 등록합니다."
    )
    @PostMapping("/my")
    public ResponseEntity<MessageDto> addMyModel(@RequestBody AddMyModelReqDto addMyModelReqDto) {
        myModelService.addMyModel(addMyModelReqDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MessageDto.message("내 가전제품 등록 완료"));
    }

    @Operation(
            summary = "등록한 내 가전제품 목록 조회하기",
            description = "등록한 내 가전제품 전체 목록을 조회합니다."
    )
    @GetMapping("/my")
    public ResponseEntity<List<FindMyModelListResDto>> findMyModelList(@RequestParam("category")String category) {
        return ResponseEntity.ok(myModelService.findMyModelList(category));
    }

    @Operation(
            summary = "등록한 내 가전제품 상세 정보 조회하기",
            description = "등록한 내 가전제품 상세 정보를 조회합니다."
    )
    @GetMapping("/my/{myModelId}")
    public ResponseEntity<FindMyModelResDto> findMyModel(@PathVariable long myModelId) {
        return ResponseEntity.ok(myModelService.findMyModel(myModelId));
    }


    @Operation(
            summary = "등록한 내 가전제품 삭제하기",
            description = "등록한 내 가전제품을 삭제합니다."
    )
    @DeleteMapping("/my/{modelId}")
    public ResponseEntity<MessageDto> deleteMyModel(@PathVariable long modelId) {
        myModelService.deleteMyModel(modelId);
        return ResponseEntity.ok(MessageDto.message("내 가전제품 삭제 완료"));
    }

    @Operation(
            summary = "내 가전제품 핀 등록 or 취소하기",
            description = "내 가전제품을 핀 등록되어있지 않으면 등록하고 등록되어있으면 등록 취소합니다."
    )
    @PatchMapping("/my/pin/{myModelId}")
    public ResponseEntity<MessageDto> updateMyModelPinned(@PathVariable long myModelId) {
        myModelService.updateMyModelPinned(myModelId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "가전제품 찜 하기",
            description = "가전제품을 찜 합니다."
    )
    @PostMapping("/like/{modelId}")
    public ResponseEntity<MessageDto> addLikeModel(@PathVariable long modelId) {
        likeModelService.addLikeModel(modelId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MessageDto.message("가전제품 찜 완료"));
    }

    @Operation(
            summary = "가전제품 찜 취소하기",
            description = "가전제품을 찜 취소합니다."
    )
    @DeleteMapping("/like/{modelId}")
    public ResponseEntity<MessageDto> deleteLikeModel(@PathVariable long modelId) {
        likeModelService.deleteLikeModel(modelId);
        return ResponseEntity.ok(MessageDto.message("가전제품 찜 취소 완료"));
    }


}
