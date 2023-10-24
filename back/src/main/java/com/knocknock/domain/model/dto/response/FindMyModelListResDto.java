package com.knocknock.domain.model.dto.response;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindMyModelListResDto {

    Long myModelId;

    Long modelId;

    String category;

    String modelBrand;

    Integer modelGrade;

    String modelNickname;

    Date addAtPin;

}
