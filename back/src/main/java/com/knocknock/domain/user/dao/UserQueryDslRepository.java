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

import static com.knocknock.domain.user.domain.QUsers.users;

@Repository
@RequiredArgsConstructor
public class UserQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<Users> findByCondition(UserSearchCondition condition) {
        return jpaQueryFactory.selectFrom(users)
                .where(containsName(condition.getNickName()),
                        containsAddress(condition.getAddress()),
                        sameUserType(condition.getUserType()))
                .limit((condition.getMax() == null || condition.getMax() == 0) ? 10 : condition.getMax())
                .offset((condition.getOffset() == null || condition.getOffset() == 0) ? 0 : condition.getOffset())
                .orderBy(users.nickname.asc())
                .fetch();
    }

//    /**
//     * 소셜 회원만 체크
//     */
//    private BooleanExpression isSocialUser(EnumPath<UserType> userType) {
//        return userType.eq(UserType.ROLE_SOCIAL);
//    }
//
//    /**
//     * 일반 회원만 체크
//     */
//    private BooleanExpression isNormalUser(EnumPath<UserType> userType) {
//        return userType.eq(UserType.ROLE_USER);
//    }

    private BooleanExpression sameUserType(String userType) {
        if(userType == null)
            return null;

        return users.userType.eq(UserType.getUserType(userType));
    }

    /**
     * 해당 이름을 닉네임에 포함하고 있는지 체크
     */
    private BooleanExpression containsName(String name) {
        if(name == null)
            return null;

        return users.nickname.contains(name)
                .or(users.nickname.startsWith(name))
                .or(users.nickname.endsWith(name));
    }

    /**
     * 해당 주소에 살고 있는 유저인지 체크
     */
    private BooleanExpression containsAddress(String address) {
        if(address == null)
            return null;

        return users.address.contains(address)
                .or(users.address.startsWith(address))
                .or(users.address.endsWith(address));
    }




}
