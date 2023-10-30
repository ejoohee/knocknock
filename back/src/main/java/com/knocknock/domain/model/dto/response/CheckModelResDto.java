package com.knocknock.domain.model.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckModelResDto {

    private String category;

    private String modelName;

    private String modelBrand;

    private String modelImg;

}
