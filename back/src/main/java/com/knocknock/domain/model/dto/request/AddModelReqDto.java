package com.knocknock.domain.model.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
@Builder
public class AddModelReqDto {

    private Boolean error;

    private Integer grade;

    private String img;

    private String url;

    private Float usageValue1;

    private Float usageValue2;

    private Float usageValue3;

    private Integer co2;

    private Integer cost;


}
