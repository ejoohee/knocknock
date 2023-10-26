package com.knocknock.domain.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSearchCondition {

    String option; // random & like & pin & all & rank 등등
    String name;
    String userType;
    Long userId;
    String address;

    Integer page;
    Integer max;

}
