package com.knocknock.domain.user.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.knocknock.domain.user.dao.RefreshTokenRedisRepository;
import com.knocknock.domain.user.dao.UserRepository;
import com.knocknock.domain.user.domain.RefreshToken;
import com.knocknock.domain.user.domain.UserType;
import com.knocknock.domain.user.domain.Users;
import com.knocknock.domain.user.dto.response.SocialLoginResDto;
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
                            .userType("소셜회원")
                            .nickname(nickname)
                            .build());
                });
        // Redis에 refreshToken 저장
        // 회원의 이메일(ID)을 키로 저장
        // saveRefreshTokenToRedis(email, refreshToken);

        // Spring Security Context에 인증 정보 저장
        authenticateUser(user);

        return SocialLoginResDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(email)
                .address(user.getAddress())
                .nickname(user.getNickname())
                .build();
    }

    private void validateAccessToken(String oauthAccessToken) {
        if (oauthAccessToken == null) {
            log.error("[소셜 유저 로그인] Access token 가져오기 실패");
            throw new UserNotFoundException(UserExceptionMessage.ACCESS_TOKEN_NOT_FOUND.getMessage());
        }
    }

    private void validateUserResource(JsonNode userResourceNode) {
        if (userResourceNode == null || !userResourceNode.has("email")) {
            log.error("[소셜 유저 로그인] 사용자 정보 가져오기 실패");
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
        return accessTokenNode.get("access_token").asText();
    }

    private JsonNode getUserResource(String accessToken) {
        String resourceUri = oauth2Config.getResourceUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity entity = new HttpEntity(headers);
        return restTemplate.exchange(resourceUri, HttpMethod.GET, entity, JsonNode.class).getBody();
    }
}
