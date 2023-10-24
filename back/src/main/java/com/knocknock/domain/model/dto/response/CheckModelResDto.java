package com.knocknock.domain.model.dto.response;

import lombok.Builder;
import lombok.Setter;

@Setter
@Builder
public class CheckModelResDto {

    String category;

    String modelName;

    String modelBrand;

    String modelImg;

}
