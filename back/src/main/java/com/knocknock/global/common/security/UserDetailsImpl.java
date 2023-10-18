package com.knocknock.global.common.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.knocknock.domain.user.domain.UserType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class UserDetailsImpl implements UserDetails {

    private Long userId;
    private String email;

    @JsonIgnore
    private String password; // getPassword 오버라이드 안해도됨
    private String authority;

    public UserDetailsImpl(Long userId, String email, String password, String authority) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.authority = authority;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> auth = new ArrayList<>();

        if(authority.equals(UserType.ROLE_ADMIN.getValue()))
            authority = "ROLE_ADMIN";
        else
            authority = "ROLE_USER";

        auth.add(new SimpleGrantedAuthority(authority));
        return auth;
    }



    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
