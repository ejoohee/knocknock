package com.knocknock.domain.model.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindMyModelResDto {

    private Long myModelId;
    private Long modelId;
    private String category;
    private String modelName;
    private String modelBrand;
    private Integer modelGrade;
    private String modelImg;
    private String usage1;
    private Float usageValue1;
    private String usageUnit1;
    private String usage2;
    private Float usageValue2;
    private String usageUnit2;
    private String usage3;
    private Float usageValue3;
    private String usageUnit3;
    private Integer modelCo2;
    private String co2Unit;
    private Integer modelCost;
    private String costUnit;
    private String addAtPin;

}
