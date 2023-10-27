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

    Integer usageValue1;

    Integer usageValue2;

    Integer usageValue3;

    Integer co2;

    Integer cost;


}
