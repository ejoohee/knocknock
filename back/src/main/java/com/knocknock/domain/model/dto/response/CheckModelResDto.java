package com.knocknock.domain.model.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckModelResDto {

    String category;

    String modelName;

    String modelBrand;

    String modelImg;

}
