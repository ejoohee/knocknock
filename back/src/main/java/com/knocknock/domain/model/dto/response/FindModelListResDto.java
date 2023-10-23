package com.knocknock.domain.model.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindModelListResDto {

    Long modelId;

    String modelName;

    String modelBrand;

    Integer modelGrade;

    Boolean isLiked;

}
