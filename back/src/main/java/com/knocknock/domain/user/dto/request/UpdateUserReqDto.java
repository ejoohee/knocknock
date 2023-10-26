package com.knocknock.domain.user.dto.request;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class UpdateUserReqDto {

    private String nickname;
    private String address;

    @Length(min = 10, max = 10, message = "지로코드는 숫자 10자리를 입력해야 합니다.")
    private String giroCode;

}
