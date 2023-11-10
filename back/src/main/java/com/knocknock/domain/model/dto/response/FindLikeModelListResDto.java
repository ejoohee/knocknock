package com.knocknock.domain.model.dto.response;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindLikeModelListResDto {

    private Long likeModelId;

    private Long modelId;

    private String modelName;

    private String modelBrand;

}

