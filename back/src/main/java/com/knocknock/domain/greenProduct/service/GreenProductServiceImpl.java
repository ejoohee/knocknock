package com.knocknock.domain.greenProduct.service;

import com.knocknock.domain.greenProduct.dao.GreenProductRepository;
import com.knocknock.domain.greenProduct.domain.GreenProduct;
import com.knocknock.domain.greenProduct.dto.response.GreenProductDataResDto;
import com.knocknock.domain.greenProduct.dto.response.GreenProductResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class GreenProductServiceImpl implements GreenProductService {

    @Value("${spring.api.base.url}")
    private String API_BASE_URL;

    @Value("${spring.api.cert.key}")
    private String CERT_KEY;

    private final GreenProductRepository greenProductRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

    @Scheduled(cron = "0 47 6 * * *") // 매일 자정에 실행
    public void scheduledFetchAndStoreProductData() {
        fetchAndStoreProductData();
    }

    @Override
    @Transactional(readOnly = true)
    public void fetchAndStoreProductData() {
        int i = 379000; // 초기 인덱스 설정
        while (true) {
            String prodIxid = "KR" + i;
            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("certKeyc", CERT_KEY);
            uriVariables.put("prodIxid", prodIxid);
            String url = API_BASE_URL + "?certKeyc={certKeyc}&prodIxid={prodIxid}";

            try {
                GreenProductResDto response = restTemplate.getForObject(url, GreenProductResDto.class, uriVariables);
                if (response == null || response.getData() == null || response.getData().isEmpty()) {
                    log.warn("[녹색정보 저장] 더 이상 가져올 녹색 제품 데이터가 없음, prodIxid: {}", prodIxid);
                    break; // 데이터가 없으면 루프 종료
                }
                GreenProductResDto.GreenProductData data = response.getData().get(0);
                saveGreenProduct(data);
            } catch (Exception e) {
                log.error("[녹색정보 저장] 녹색 제품 데이터 가져오기 및 저장 중 오류 발생, prodIxid: {}", prodIxid, e);
            }
            i++;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void saveGreenProduct(GreenProductResDto.GreenProductData data) {
        GreenProduct existingProduct = greenProductRepository.findByProdIxid(data.getProdIxid());
        if (existingProduct != null) {
            // prodRsnm 값이 변경되었는지 확인
            if (!existingProduct.getProdRsnm().equals(data.getProdRsnm())) {
                existingProduct.updateProdRsnm(data.getProdRsnm());
                greenProductRepository.save(existingProduct);
                log.info("[녹색정보 업데이트] prodIxid: {}", data.getProdIxid());
            }
            // 값이 같으면 업데이트 없이 pass
        } else {
            GreenProduct newProduct = buildGreenProduct(data);
            greenProductRepository.save(newProduct);
            log.info("[녹색정보 신규 저장] prodIxid: {}", data.getProdIxid());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public GreenProduct buildGreenProduct(GreenProductResDto.GreenProductData data) {
        return GreenProduct.builder()
                .prodMdel(data.getProdMdel())
                .prodVcnm(data.getProdVcnm())
                .prodIxid(data.getProdIxid())
                .prodInrs(data.getProdInrs())
                .prodRsdt(data.getProdRsdt())
                .prodRedt(data.getProdRedt())
                .prodRsnm(data.getProdRsnm())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GreenProductDataResDto> searchByProductNameOrCompanyName(String keyword) {
        return greenProductRepository.findByProdMdelContainingOrProdVcnmContaining(keyword, keyword)
                .stream()
                .map(product -> {
                    return new GreenProductDataResDto(
                            product.getProdIxid(),
                            product.getProdMdel(),
                            product.getProdVcnm(),
                            product.getProdInrs(),
                            product.getProdRsdt(),
                            product.getProdRedt(),
                            product.getProdRsnm()
                    );
                })
                .collect(Collectors.toList());
    }
}
