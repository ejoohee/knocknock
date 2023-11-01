package com.knocknock.domain.model.dto.response;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindMyModelListResDto {

    private Long myModelId;

    private Long modelId;

    private String category;

    private String modelBrand;

    private Integer modelGrade;

    private String modelNickname;

    private String addAtPin;

}
