package com.knocknock.domain.model.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
@Builder
public class AddModelReqDto {

    Boolean error;

    Integer grade;

    String img;

    String url;

    Float usageValue1;

    Float usageValue2;

    Float usageValue3;

    Integer co2;

    Integer cost;


}
