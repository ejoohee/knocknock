package com.knocknock.domain.greenProduct.service;

import com.knocknock.domain.greenProduct.dao.GreenProductRepository;
import com.knocknock.domain.greenProduct.domain.GreenProduct;
import com.knocknock.domain.greenProduct.dto.response.GreenProductResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class GreenProductServiceImpl implements GreenProductService {

    private static final String API_BASE_URL = "https://www.greenproduct.go.kr/open-api/open-api/rest/GreenProductInformationInquiryService2.do";
    private static final String CERT_KEY = "b7c7f3a73086bec7f2f39445f24e478cfbc8068389d38f66a1848c5c062f128f";

    private final GreenProductRepository greenProductRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

    @Scheduled(cron = "0  0 * * *") // 매일 자정에 실행
    public void scheduledFetchAndStoreProductData() {
        fetchAndStoreProductData();
    }

    @Transactional
    public void fetchAndStoreProductData() {
        int i = 200000; // 초기 인덱스 설정
        //마지막 : 379489
        while (true) {
            String prodIxid = "KR" + i;
            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("certKeyc", CERT_KEY);
            uriVariables.put("prodIxid", prodIxid);
            String url = API_BASE_URL + "?certKeyc={certKeyc}&prodIxid={prodIxid}";

            try {
                GreenProductResDto response = restTemplate.getForObject(url, GreenProductResDto.class, uriVariables);
                if (response == null || response.getData() == null || response.getData().isEmpty()) {
                    log.warn("[녹색정보 저장] 더 이상 가져올 녹색 제품 데이터가 없음, 마지막 prodIxid: {}", prodIxid);
                    break; // 데이터가 없으면 루프 종료
                }
                GreenProductResDto.GreenProductData data = response.getData().get(0);
                saveGreenProduct(data);
            } catch (Exception e) {
                log.error("[녹색정보 저장] 녹색 제품 데이터 가져오기 및 저장 중 오류 발생, prodIxid: {}", prodIxid, e);
            }

            i++;
            if (i == 200010) break;
        }
    }

    @Transactional
    public void saveGreenProduct(GreenProductResDto.GreenProductData data) {
        GreenProduct existingProduct = greenProductRepository.findByProdIxid(data.getProdIxid());
        if (existingProduct != null) {
            // prodRsnm 값이 변경되었는지 확인
            if (!existingProduct.getProdRsnm().equals(data.getProdRsnm())) {
                existingProduct.updateProdRsnm(data.getProdRsnm());
                greenProductRepository.save(existingProduct);
            }
            // 값이 같으면 업데이트 없이 pass
        } else {
            GreenProduct newProduct = buildGreenProduct(data);
            greenProductRepository.save(newProduct);
        }
    }

    @Transactional
    public GreenProduct buildGreenProduct(GreenProductResDto.GreenProductData data) {
        return GreenProduct.builder()
                .prodMdel(data.getProdMdel())
                .prodVcnm(data.getProdVcnm())
                .prodInrs(data.getProdInrs())
                .prodRsdt(data.getProdRsdt())
                .prodRedt(data.getProdRedt())
                .prodRsnm(data.getProdRsnm())
                .build();
    }

    @Transactional(readOnly = true)
    public List<GreenProduct> searchByProductNameOrCompanyName(String keyword) {
        return greenProductRepository.findByProdMdelContainingOrProdVcnmContaining(keyword, keyword);
    }
}
