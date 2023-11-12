package com.knocknock.domain.waste.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WasteResDto {
    @JsonProperty("ENTRPS_NM")
    String enterpriseName;
    @JsonProperty("ADRES")
    String address;
    @JsonProperty("TELNO")
    String telNo;

    @JsonProperty("WSTE")
    String wasteType;
}
