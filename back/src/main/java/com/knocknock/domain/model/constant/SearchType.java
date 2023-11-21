package com.knocknock.domain.model.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchType {

    BRAND("brand"),
    MODEL("model");

    private String value;

}
