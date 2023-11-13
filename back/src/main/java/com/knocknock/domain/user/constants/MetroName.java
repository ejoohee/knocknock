package com.knocknock.domain.user.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 프론트에서 넘어오는 값을 metro 값에 맞게 변경 (ex: 서울 -> 서울특별시)
 */
@Getter
@AllArgsConstructor
public enum MetroName {

    서울("서울", "서울특별시"),
    강원도("강원", "강원도"),
    경기도("경기", "경기도"),
    경상남도("경남", "경상남도"),
    경상북도("경북", "경상북도"),
    광주광역시("광주", "광주광역시"),
    대구광역시("대구", "대구광역시"),
    대전광역시("대전", "대전광역시"),
    부산광역시("부산", "부산광역시"),
    세종특별자치시("세종특별자치시", "세종특별자치시"),
    울산광역시("울산", "울산광역시"),
    인천광역시("인천", "인천광역시"),
    전라남도("전남", "전라남도"),
    전라북도("전북", "전라북도"),
    제주특별자치도("제주특별자치도", "제주특별자치도"),
    충청남도("충남", "충청남도"),
    충청북도("충북", "충청북도");

    private String origin;
    private String converted;

    public static String getConverted(String origin) {
        for (MetroName metroName: MetroName.values()) {
            if(metroName.origin.equals(origin)){
                return metroName.getConverted();
            }
        }
        return null;
    }

}
