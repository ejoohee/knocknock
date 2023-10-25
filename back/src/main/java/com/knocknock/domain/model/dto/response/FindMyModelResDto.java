package com.knocknock.domain.model.dto.response;

import lombok.*;

import java.util.Date;

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
    private String modelURL;
    private String usage;
    private Integer modelUsage;
    private String usageUnit;
    private String usage2;
    private Integer modelUsage2;
    private String usageUnit2;
    private String usage3;
    private Integer modelUsage3;
    private String usageUnit3;
    private Integer modelCo2;
    private Integer modelCost;
    private Date addAtPin;

}
