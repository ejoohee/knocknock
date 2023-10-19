package com.knocknock.global.config;

import com.knocknock.global.common.security.AccessDeniedHandlerIml;
import com.knocknock.global.common.security.AuthenticationEntryPointImpl;
import com.knocknock.global.common.security.AuthenticationJwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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


//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return web -> web.ignoring().antMatchers("/resources/**");
//    }

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
        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(authenticationJwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
                .and()
                .authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic().and().formLogin().disable();




//        http.formLogin()
//                .loginPage("/login")
//                .permitAll();
//
//        http.logout()
//                .logoutSuccessUrl("/");

        return http.build();
    }

    /**
     * Resource용 SecurityFilterChain 적용
     * resources의 경우 securityContext 등에 대한 조회가 불필요하므로 disable
     */
    @Bean
    @Order(0) // 먼저 FilterChain을 타도록 지정
    public SecurityFilterChain resources(HttpSecurity http) throws Exception {
        return http.requestMatchers(matchers -> matchers.antMatchers( "/resources/**"))
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .requestCache(RequestCacheConfigurer::disable)
                .securityContext(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable).build();
    }


}
