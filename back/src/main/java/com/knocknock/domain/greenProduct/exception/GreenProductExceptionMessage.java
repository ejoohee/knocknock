package com.knocknock.domain.greenProduct.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GreenProductExceptionMessage {

    GREEN_PRODUCT_NOT_FOUND("녹색 제품을 찾을 수 없습니다.");

    private String message;

}
