package com.knocknock.global.common.ocr.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageInfoDto {

    private String format;
    private String name;
    private String data;

}
