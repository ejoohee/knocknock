package com.knocknock.domain.email.service;

import com.knocknock.domain.email.domain.EmailMessage;
import com.knocknock.domain.email.dto.EmailCodeReqDto;
import com.knocknock.domain.email.dto.EmailCodeResDto;
import com.knocknock.domain.email.dto.EmailPostDto;

public interface EmailService {

    EmailCodeResDto sendEmail(EmailPostDto emailPostDto, String type);

    Boolean checkEmailCode(EmailCodeReqDto emailCodeReqDto);




}
