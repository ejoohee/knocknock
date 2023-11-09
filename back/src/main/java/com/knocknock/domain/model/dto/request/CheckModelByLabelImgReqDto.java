package com.knocknock.domain.model.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckModelByLabelImgReqDto {

    // base64 인코딩된 값
    private String labelImg;

}
