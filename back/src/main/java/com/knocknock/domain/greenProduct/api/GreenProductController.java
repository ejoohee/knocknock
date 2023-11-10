package com.knocknock.domain.greenProduct.api;

import com.knocknock.domain.greenProduct.domain.GreenProduct;
import com.knocknock.domain.greenProduct.service.GreenProductService;
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
@RequestMapping("/api/greenproduct")
public class GreenProductController {

    private final GreenProductService greenProductService;

    @Operation(
            summary = "녹색 인증 제품 확인",
            description = "모델명이나 회사명으로 녹생 인증 제품인지 확인합니다."
    )
    @GetMapping("/search")
    public ResponseEntity<List<GreenProduct>> searchProducts(@RequestParam String keyword) {
        List<GreenProduct> products = greenProductService.searchByProductNameOrCompanyName(keyword);
        return ResponseEntity.ok(products);
    }


    @GetMapping("")
    public ResponseEntity<Void> fetchAndStoreGreenProducts() {
        try {
            greenProductService.fetchAndStoreProductData();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
