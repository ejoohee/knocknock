package com.knocknock.global.common.external.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 백엔드 서버에서 호출하는 외부 api 주소 enum
 */
@Getter
@AllArgsConstructor
public enum ExternalApiBaseUrl {

    ADOBE_PHOTOSHOP_CUTOUT("https://image.adobe.io/sensei/cutout"),
    DROPBOX_GET_TEMPORARY_UPLOAD_LINK("https://api.dropboxapi.com/2/files/get_temporary_upload_link"),
    KEPCO_POWER_AVG("https://bigdata.kepco.co.kr/openapi/v1/powerUsage/houseAve.do"),
    OCR_TEXT("https://wjibbfej6w.apigw.ntruss.com/custom/v1/25714/ae22652173a41a47c1a7d8825811303a9a2c989203f7d8b3857aadefee96dcb8/general"),
    ENERGY_PRODUCT_LIST_INFO("https://api.odcloud.kr/api/15083315/v1/uddi:e2de2c4d-e81b-421d-94bc-ad0f9cf70c44"),
    ENERGY_PRODUCT_INFO("http://apis.data.go.kr/B553530/eep/");

    private String url;


}
