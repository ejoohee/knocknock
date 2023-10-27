package com.knocknock.global.common.openapi.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum CategoryUsageType {

    전기세탁기_일반("전기세탁기(일반)", "CONS_PWR", null, null, null),
    전기진공청소기("전기진공청소기", "MAX_CAPA", null, null, null),
    전기세탁기_드럼("전기세탁기(드럼)", "CONS_PWR", null, null, null),
    공기청정기("공기청정기","CONS_PWR", "STANDARD_CONS_AREA", "", ""),
    전기밥솥("전기밥솥", "", "", "", ""), // 달라요
    김치냉장고("김치냉장고", "MONTH_CONS_PWR", "KIMCHI_AVAIL_CAPA", null, null),
    전기온풍기("전기온풍기","CONS_PWR", null, null, null),
    전기스토브("전기스토브", "CONS_PWR", null, null, null),
    전기냉온수기("전기냉온수기", "MONTH_CONS_PWR", "", "", ""),
    전기냉장고("전기냉장고", "MONTH_CONS_PWR", "CAPA", "", ""),
    전기레인지("전기레인지", "UNIT_CONS_PWR", "", "", ""),
    전기냉방기("전기냉방기", "MONTH_CONS_PWR1", "", "EFFIC1", ""),
    정기냉난방기("전기냉난방기", "MON_CONS_PWR", "COOL_EFFIC", "PROP_COOL_PWR", ""),
    전기냉온수기_순간식("전기냉온수기(순간식)", "", "", "", ""),
    제습기("제습기", "DHF_EFFIC", "RATE_DHF_ABTY", null, "YEAR_COST");

    private String category;
    private String usage1;
    private String usage2;
    private String usage3;
    // 월간인 경우
    private String cost;

    public static CategoryUsageType findCategoryType(String category) {
        for (CategoryUsageType categoryUsageType : CategoryUsageType.values()) {
            if(categoryUsageType.getCategory().equals(category))
                return categoryUsageType;
        }
        return null;
    }


}
