package com.knocknock.domain.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompareModelAndMyModelResDto {

    String modelAName;

    String modelBName;

    Integer modelAGrade;

    Integer modelBGrade;
    // 1년 단위
    Long modelACo2;
    Long modelBCo2;

    Integer modelACost;
    Integer modelBCost;
}
