package com.knocknock.global.common.external.ocr.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequestDto {

    private List<ImageInfoDto> images;
    private String lang;
    private String requestId;
    private String resultType;
    private long timestamp;
    private String version;

}
