package com.knocknock.domain.user.dao;

import com.knocknock.domain.user.domain.UserType;
import com.knocknock.domain.user.domain.Users;
import com.knocknock.domain.user.dto.request.UserSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

//import static com.knocknock.domain.

@Repository
@RequiredArgsConstructor
public class UserQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

//    public List<Users> findByCondition(UserSearchCondition condition) {
//
//
//
//    }

    /**
     * 소셜 회원만 체크
     */
    private BooleanExpression isSocialUser(EnumPath<UserType> userType) {
        return userType.eq(UserType.ROLE_SOCIAL);
    }

    /**
     * 일반 회원만 체크
     */
    private BooleanExpression isNormalUser(EnumPath<UserType> userType) {
        return userType.eq(UserType.ROLE_USER);
    }

//    private BooleanExpression containsName(String name) {
//        if(name == null)
//            return null;
//
//        return
//    }


}
