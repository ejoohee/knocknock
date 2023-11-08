package com.knocknock.domain.user.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.knocknock.domain.user.dao.RefreshTokenRedisRepository;
import com.knocknock.domain.user.dao.UserRepository;
import com.knocknock.domain.user.domain.RefreshToken;
import com.knocknock.domain.user.domain.UserType;
import com.knocknock.domain.user.domain.Users;
import com.knocknock.domain.user.dto.request.CheckGoogleReqDto;
import com.knocknock.domain.user.dto.request.GoogleLoginReqDto;
import com.knocknock.domain.user.dto.request.UpdateAddressReqDto;
import com.knocknock.domain.user.dto.response.SocialLoginResDto;
import com.knocknock.domain.user.dto.response.UpdateAddressResDto;
import com.knocknock.domain.user.exception.UserExceptionMessage;
import com.knocknock.domain.user.exception.UserNotFoundException;
import com.knocknock.global.common.jwt.JwtExpirationEnum;
import com.knocknock.global.config.OAuth2Config;
import com.knocknock.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSocialServiceImpl implements UserSocialService {

    private final OAuth2Config oauth2Config;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRedisRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public SocialLoginResDto socialLogin(String code) {

        String oauthAccessToken = getAccessToken(code);
        validateAccessToken(oauthAccessToken);

        JsonNode userResourceNode = getUserResource(oauthAccessToken);
        validateUserResource(userResourceNode);

        String email = userResourceNode.get("email").asText();
        String nickname = userResourceNode.get("name").asText();
        UserType isSocial = UserType.ROLE_SOCIAL;

        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);

        // 이메일과 isSocial 값을 기반으로 사용자 찾기
        Users user = userRepository.findByEmailAndUserType(email, isSocial)
                .orElseGet(() -> {
                    return userRepository.save(Users.builder()
                            .email(email)
                            .userType(isSocial.getValue())
                            .nickname(nickname)
                            .build());
                });
//         Redis에 refreshToken 저장
//         회원의 이메일(ID)을 키로 저장
        saveRefreshTokenToRedis(email, refreshToken);

        // Spring Security Context에 인증 정보 저장
        authenticateUser(user);

        log.info("[소셜 유저 로그인] 로그인 성공 - 이메일: {}", email);
        return SocialLoginResDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(email)
                .userType(isSocial.getValue())
                .address(user.getAddress())
                .nickname(user.getNickname())
                .build();
    }

    @Override
    public void checkSocialLogin(CheckGoogleReqDto checkGoogleReqDto) {
        String email = checkGoogleReqDto.getEmail();
        log.info("[소셜 이메일 확인] 이메일 확인 요청. {} ", email);

        UserType isSocial = UserType.ROLE_SOCIAL;
        // 이메일과 isSocial 값을 기반으로 사용자 찾기
        Users user = userRepository.findByEmailAndUserType(email, isSocial)
                .orElseThrow(() -> {
                    log.error("[유저 로그인] 유저를 찾을 수 없습니다.");
                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage());
                });
    }

    @Override
    public SocialLoginResDto socialFrontLogin(GoogleLoginReqDto googleLoginReqDto) {

        String email = googleLoginReqDto.getEmail();
        String nickname = googleLoginReqDto.getNickname();
        String address = googleLoginReqDto.getAddress();
        // 패스워드 암호화
        String password = passwordEncoder.encode(googleLoginReqDto.getPassword());

        log.info("[소셜 회원가입] 패스워드 암호화 완료.");

        UserType isSocial = UserType.ROLE_SOCIAL;

        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);

        // 이메일과 isSocial 값을 기반으로 사용자 찾기
        Users user = userRepository.findByEmailAndUserType(email, isSocial)
                .orElseGet(() -> {
                    return userRepository.save(Users.builder()
                            .email(email)
                            .userType(isSocial.getValue())
                            .nickname(nickname)
                            .address(address)
                            .password(password)
                            .build());
                });
//         Redis에 refreshToken 저장
//         회원의 이메일(ID)을 키로 저장
        saveRefreshTokenToRedis(email, refreshToken);

        // Spring Security Context에 인증 정보 저장
        authenticateUser(user);

        log.info("[소셜 유저 로그인] 로그인 성공 - 이메일: {}", email);
        return SocialLoginResDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(email)
                .userType(isSocial.getValue())
                .address(user.getAddress())
                .nickname(user.getNickname())
                .build();
    }

    private void validateAccessToken(String oauthAccessToken) {
        if (oauthAccessToken == null) {
            log.error("[소셜 유저 로그인] 토큰 획득 실패");
            throw new UserNotFoundException(UserExceptionMessage.ACCESS_TOKEN_NOT_FOUND.getMessage());
        }
    }

    private void validateUserResource(JsonNode userResourceNode) {
        if (userResourceNode == null || !userResourceNode.has("email")) {
            log.error("[소셜 유저 로그인] 사용자 정보 획득 실패");
            throw new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage());
        }
    }

    private void saveRefreshTokenToRedis(String email, String refreshToken) {
        refreshTokenRepository.save(RefreshToken.builder()
                .email(email)
                .refreshToken(refreshToken)
                .expiration(JwtExpirationEnum.REFRESH_TOKEN_EXPIRATION_TIME.getValue() / 1000)
                .build());
    }

    private void authenticateUser(Users user) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getUserType().getValue());
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user,
                null, //소셜은 password 없으니까
                Collections.singletonList(authority)
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private String getAccessToken(String authorizationCode) {
        log.info("[소셜 유저 로그인] 토큰 요청");
        String clientId = oauth2Config.getClientId();
        String clientSecret = oauth2Config.getClientSecret();
        String redirectUri = oauth2Config.getRedirectUri();
        String tokenUri = oauth2Config.getTokenUri();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity entity = new HttpEntity(params, headers);

        ResponseEntity<JsonNode> responseNode = restTemplate.exchange(tokenUri, HttpMethod.POST, entity, JsonNode.class);
        JsonNode accessTokenNode = responseNode.getBody();
        log.info("[소셜 유저 로그인] 토큰 획득 성공");
        return accessTokenNode.get("access_token").asText();
    }

    private JsonNode getUserResource(String accessToken) {
        log.info("[소셜 유저 로그인] 리소스 요청");
        String resourceUri = oauth2Config.getResourceUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity entity = new HttpEntity(headers);
        log.info("[소셜 유저 로그인] 리소스 획득 성공");
        return restTemplate.exchange(resourceUri, HttpMethod.GET, entity, JsonNode.class).getBody();
    }

    @Override
    public UpdateAddressResDto updateAddress(UpdateAddressReqDto updateAddressReqDto, String token) {
        log.info("[소셜 유저 로그인] 주소 변경 요청");
        Users loginUser = getLoginUser(token);

        loginUser.updateAddress(updateAddressReqDto.getAddress());
        userRepository.save(loginUser);

        log.info("[소셜 유저 로그인] 주소 변경 성공");
        return UpdateAddressResDto.builder()
                .address(loginUser.getAddress())
                .build();
    }

    private Users getLoginUser(String token) {
        String email = jwtUtil.getLoginEmail(token);

        Users loginUser = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[UserService] 로그인 유저를 찾을 수 없습니다.");
                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage());
                });

        return loginUser;
    }
}
