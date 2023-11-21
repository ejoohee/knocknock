package com.knocknock.domain.user.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindPowerUsageHouseAvgResDto {

    private Integer year;

    private Integer month;

    private Float powerUsage;

    private Integer bill;

}
