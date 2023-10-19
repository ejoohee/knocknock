package com.knocknock.domain.user.dto.password;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordReqDto {

    private String password;
}
