package com.knocknock.global.config;

import com.knocknock.global.common.security.AccessDeniedHandlerIml;
import com.knocknock.global.common.security.AuthenticationEntryPointImpl;
import com.knocknock.global.common.security.AuthenticationJwtFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity // 스프링 시큐리티 활성화
@RequiredArgsConstructor
public class SecurityConfig  {

    private final AuthenticationJwtFilter authenticationJwtFilter;
    private final AccessDeniedHandlerIml accessDeniedHandler;
    private final AuthenticationEntryPointImpl authenticationEntryPoint;

    // UserService에서 passwordEncoder를 사용하기 위해 빈 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static final String[] PERMIT_URL_ARRAY = {
        // swagger
            "/favicon.ico",
            "/error",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",

        // user
            "/api/user/login",
            "/api/user/sign-up",
            "/api/user/login/google",

        // email
            "/api/email/**",

        // model



    };


    /*
antMatchers("/test")는 정확한 /test URL만 일치.
mvcMatchers("/test")는 /test, /test/, /test.html, /test.xyz 등 다양하게 일치.
==> antMatchers("/test/**") == mvcMatchers("/test")

화이트리스트 방식 (권장) - 모든 endpoint를 "인증필요"로 관리하고 특정 endpoint만 "허용"
블랙리스트 방식 - 모든 endpoint를 "허용"하고 특정 endpoint만 "인증필요"상태로 관리
https://velog.io/@topy/antMatchers-vs-mvcMatchers

     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // h2-console local 연결
        http.authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .and()
                .headers()
                .frameOptions()
                .disable()
                .and()
                .csrf()
                .ignoringAntMatchers("/h2-console/**").disable();

         http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/user/password").permitAll()
                .antMatchers(PERMIT_URL_ARRAY).permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic().disable()
                .formLogin().disable()
                .cors().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(authenticationJwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        log.info("[SecurityConfig] filterChain 완료! ");
         return http.build();
    }
}
