package com.knocknock.domain.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompareModelAndMyModelResDto {

    private String modelAName;

    private String modelBName;

    private Integer modelAGrade;

    private Integer modelBGrade;
    // 1년 단위
    private Long modelACo2;

    private Long modelBCo2;

    private Integer modelACost;

    private Integer modelBCost;

}
