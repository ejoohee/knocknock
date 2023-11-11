package com.knocknock.global.common.openapi.airInfo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDto {

    private String rnAdres; // 도로명
    private String lnmAdres; // 지번주소

    private String 읍면동;
    private String 도로명주소;
    private String 지번주소;

//    public static AddressDto jsonToDto(JSONObject object) {
//        String[] lnm = object.get("lnmAdres").toString().split(" ");
//
//        return AddressDto.builder()
//                .도로명주소(object.get("rnAdres").toString())
//                .지번주소(object.get("lnmAdres").toString())
//                .읍면동(findDong(lnm))
//                .build();
//    }
//
//    private static String findDong(String[] addressList) {
//        for(String address : addressList) {
//            if(address.charAt(address.length() - 1) == '동') {
//                // 마지막 글자가 동이고,
//
//                return address;
//                // ㄸ 뭘 체크해야대징
//            }
//        }
//
//        for(String address : addressList) {
//            if(address.charAt(address.length() - 1) == '면')
//                return address;
//        }
//
//        for(String address : addressList) {
//            if(address.charAt(address.length() - 1) == '읍')
//                return address;
//        }
//
//        return null;
//    }


}
