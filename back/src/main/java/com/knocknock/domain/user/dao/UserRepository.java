package com.knocknock.domain.user.dao;

import com.knocknock.domain.user.domain.UserType;
import com.knocknock.domain.user.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    Optional<Users> findByEmailAndUserType(String email, UserType userType);

}
