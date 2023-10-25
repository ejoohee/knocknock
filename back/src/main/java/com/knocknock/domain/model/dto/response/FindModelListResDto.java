package com.knocknock.domain.model.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindModelListResDto {

    private Long modelId;

    private String modelName;

    private String modelBrand;

    private Integer modelGrade;

    private Boolean isLiked;

}
