package com.knocknock.domain.user.api;

import com.knocknock.domain.user.dao.UserRepository;
import com.knocknock.domain.user.dto.response.SocialLoginResDto;
import com.knocknock.domain.user.service.UserSocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/user", produces = "application/json")
public class UserSocialController {


    private final UserSocialService userSocialService;
    private final UserRepository userRepository;

    @GetMapping("/login/google")
    public ResponseEntity<SocialLoginResDto> googleLogin(@RequestParam String code) {
        return ResponseEntity.ok(userSocialService.socialLogin(code));
    }


}
