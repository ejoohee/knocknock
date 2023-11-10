package com.knocknock.domain.greenProduct.service;

import com.knocknock.domain.greenProduct.domain.GreenProduct;
import com.knocknock.domain.greenProduct.dto.response.GreenProductResDto;

import java.util.List;

public interface GreenProductService {

    void fetchAndStoreProductData();

    void saveGreenProduct(GreenProductResDto.GreenProductData data);

    GreenProduct buildGreenProduct(GreenProductResDto.GreenProductData data);

    List<GreenProduct> searchByProductNameOrCompanyName(String keyword);
//    List<GreenProductResDto> searchByProductNameOrCompanyName(String keyword);
}


