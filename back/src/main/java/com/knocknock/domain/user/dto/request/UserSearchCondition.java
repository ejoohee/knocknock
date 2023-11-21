package com.knocknock.domain.user.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSearchCondition {

//    String option; // random & like & all & rank
    String nickName; // 포함
    String address; // 포함
    String userType; // 일치


    Integer page;
    Integer max;

    public Integer getOffset() {
        if(page == null || max == null)
            return 0;

        return max * page;
    }

}
