package com.knocknock.global.common.openapi.constants;


import lombok.*;

// 한국에너지공단_에너지소비효율 등급 제품 정보
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CategoryURL {

        전기세탁기_일반("전기세탁기(일반)", "EEP_01_LIST", "세탁기"),
    전기진공청소기("전기진공청소기", "EEP_05_LIST", "진공청소기"),
    전기세탁기_드럼("전기세탁기(드럼)", "EEP_06_LIST", "세탁기"),
    공기청정기("공기청정기", "EEP_08_LIST", "공기청정기"),
//    전기밥솥("전기밥솥", "EEP_11_LIST", "전기밥솥"),
    김치냉장고("김치냉장고", "EEP_13_LIST", "김치냉장고"),
    전기온풍기("전기온풍기", "EEP_14_LIST", "온풍기"),
    전기스토브("전기스토브", "EEP_15_LIST", "전기난로"),
    전기냉온수기("전기냉온수기", "EEP_16_LIST", "정수기"),
    전기냉장고("전기냉장고", "EEP_20_LIST", "냉장고"),
    전기레인지("전기레인지", "EEP_21_LIST", "전기레인지"),
    전기냉방기("전기냉방기", "EEP_24_LIST", "에어컨"),
    텔레비전수상기("텔레비전수상기", "EEP_17_LIST", "TV"),
//    정기냉난방기("전기냉난방기", "EEP_28_LIST", "에어컨"),
//    전기냉온수기_순간식("전기냉온수기(순간식)", "EEP_30_LIST", "정수기");
    제습기("제습기", "EEP_19_LIST", "제습기");


    private String category;
    private String url;
    private String convertedCategory;

    public static CategoryURL findtCategoryUrl(String value) {
        for (CategoryURL categoryURL : CategoryURL.values()) {
            if(categoryURL.getCategory().equals(value))
                return categoryURL;
        }
        return null;
    }



}
