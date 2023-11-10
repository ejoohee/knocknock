package com.knocknock.domain.greenProduct.dto.response;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GreenProductResDto {
    private List<GreenProductData> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GreenProductData {
        private String prodIxid;
        private String prodMdel; // 제품명
        private String prodVcnm; // 회사명
        private String prodInrs; // 상세이유
        private String prodRsdt; // 인증시작일
        private String prodRedt; // 인증만료일
        private String prodRsnm; // 인증, 비인증
    }
}
