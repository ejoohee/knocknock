package com.knocknock.domain.email;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailMessage {

    private String userEmail; //수신자
    private String subject; // 메일 제목
    private String text; // 메일 내용

}
