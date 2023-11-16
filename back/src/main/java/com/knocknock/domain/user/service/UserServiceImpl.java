package com.knocknock.domain.user.service;

import com.knocknock.domain.user.constants.MetroName;
import com.knocknock.domain.airInfo.dao.AirInfoRepository;
import com.knocknock.domain.airInfo.domain.AirStation;
import com.knocknock.domain.user.dao.*;
import com.knocknock.domain.user.domain.CityCode;
import com.knocknock.domain.user.domain.LogoutAccessToken;
import com.knocknock.domain.user.domain.RefreshToken;
import com.knocknock.domain.user.domain.Users;
import com.knocknock.domain.user.dto.password.FindPasswordReqDto;
import com.knocknock.domain.user.dto.password.PasswordReqDto;
import com.knocknock.domain.user.dto.password.UpdatePasswordReqDto;
import com.knocknock.domain.user.dto.request.*;
import com.knocknock.domain.user.dto.request.UserSearchCondition;
import com.knocknock.domain.user.dto.response.*;
import com.knocknock.domain.user.exception.UserExceptionMessage;
import com.knocknock.domain.user.exception.UserException;
import com.knocknock.domain.user.exception.UserNotFoundException;
import com.knocknock.domain.user.exception.UserUnAuthorizedException;
import com.knocknock.global.common.jwt.JwtExpirationEnum;
import com.knocknock.global.common.external.kepco.KepcoAPIWebClient;
import com.knocknock.global.exception.exception.NotFoundException;
import com.knocknock.global.exception.exception.TokenException;
import com.knocknock.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final KepcoAPIWebClient kepcoAPIWebClient;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final CityCodeRepository cityCodeRepository;
    private final UserRepository userRepository;
    private final UserQueryDslRepository userQueryDslRepository;
    private final RefreshTokenRedisRepository refreshTokenRepository;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private final AirInfoRepository airInfoRepository;

    /**
     * 회원가입 정보의 유효성을 확인합니다.
     * 유효하면 true / 에러시 false
     */
    private Boolean checkSignupInfo(UserReqDto userReqDto) {
        if(userReqDto.getEmail() == null || userReqDto.getEmail().equals("") ||
           userReqDto.getPassword() == null || userReqDto.getPassword().equals("") ||
           userReqDto.getNickname() == null || userReqDto.getNickname().equals("")) {
            return false;
        }

        return true;
    }

    /**
     * 프론트에서 이메일 인증번호 받기 버튼 클릭
     * -> api/user/send-email 호출해서 인증 이메일 발송
     *
     * signUp 메서드가 호출되는 순간은
     * 회원가입 폼을 다 작성완료하고, 이메일 인증까지 완료한 다음에
     * 프론트에서 회원가입 버튼을 클릭할 때 호출!!
     *
     * 즉 signUp 메서드는 회원가입 완료에 대한 부분만 다룬다.
     */
    @Transactional
    @Override
    public void signUp(UserReqDto userReqDto) {
        String email = userReqDto.getEmail();
        log.info("[유저 회원가입] 회원가입 요청. email : {}", email);

//        // 중복 이메일 체크 ---> 이메일 인증 전에 체크(EmailService)
//        if(userRepository.existsByEmail(email)) {
//            log.error("[유저 회원가입] 이미 존재하는 이메일입니다.");
//            throw new UserException(UserExceptionMessage.EMAIL_DUPLICATED.getMessage());
//        }

        // 회원가입 정보 유효성 확인
        if(!checkSignupInfo(userReqDto)) {
            log.error("[유저 회원가입] 회원가입 정보 유효성 불일치.");
            throw new UserException(UserExceptionMessage.SIGN_UP_NOT_VALID.getMessage());
        }

        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        // 이메일 인증 여부 확인
        String checkResult = (String) valueOperations.get(userReqDto.getEmail());
        if(checkResult == null || !checkResult.equals("인증완료"))
            throw new UserException(UserExceptionMessage.EMAIL_CHECK_FAILED.getMessage());

        log.info("[유저 회원가입] 이메일 인증 완료!");

        // 패스워드 암호화
        userReqDto.setPassword(passwordEncoder.encode(userReqDto.getPassword()));
        log.info("[유저 회원가입] 패스워드 암호화 완료.");

        // 사용자의 주소를 이용하여 대기정보 측정소 찾아서 저장하기
        String stationName = updateAirStation(email);

        Users user = userRepository.save(userReqDto.dtoToEntity());
        log.info("[유저 회원가입] 유저 생성 완료!!! 회원가입이 완료되었습니다!");
    }

    @Transactional
    @Override
    public LoginResDto login(LoginReqDto loginReqDto) {
        String email = loginReqDto.getEmail();
        log.info("[유저 로그인] 로그인 요청. {} ", email);

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[유저 로그인] 유저를 찾을 수 없습니다.");
                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage());
                });

        log.info("[유저 로그인] 존재하는 유저임.");

        if(!passwordEncoder.matches(loginReqDto.getPassword(), user.getPassword())) {
            log.error("[유저 로그인] 패스워드 불일치");
            throw new UserException(UserExceptionMessage.LOGIN_PASSWORD_ERROR.getMessage());
        }

        log.info("[유저 로그인] 아이디 & 패스워드 일치. 토큰 생성 실시.");

        // 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);

        log.info("[유저 로그인] 토큰 생성 완료! Redis 저장 실시.");

        // Redis에 refreshToken 저장
        // 회원의 이메일(ID)을 키로 저장
        refreshTokenRepository.save(RefreshToken.builder()
                .email(email)
                .refreshToken(refreshToken)
                .expiration(JwtExpirationEnum.REFRESH_TOKEN_EXPIRATION_TIME.getValue() / 1000)
                .build());

        log.info("[유저 로그인] Redis 저장 완료. 로그인 성공 !! ");

        log.info("[유저 로그인] 로그인은 했는데 측정소 정보가 없어서 조회할게 ~~");
        String stationName = updateAirStation(email);

        return LoginResDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .nickname(user.getNickname())
                .email(user.getEmail())
                .address(user.getAddress())
                .build();
    }


    /**
     * 회원가입 또는 정보수정 시
     * 사용자의 주소 정보를 이용하여 가장 근접한 airStation을 찾아
     * 유저의 airStation에 저장합니다.
     *
     * 만약 로그인을 했는데 측정소 정보가 없을시에도 실행됩니다.
     */

    @Transactional
    private String updateAirStation(String email) {
        // 로그인 유저를 가져온다.
        Users loginUser = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[대기측정소 저장] 존재하지 않는 회원입니다.");
                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage());
                });

        log.info("[대기측정소 저장] email : {}, address : {}", loginUser.getEmail(), loginUser.getAddress());

        // 유저의 주소에서 지역명을 뽑아온다.
        String[] addressList = loginUser.getAddress().split(" ");
        String region = addressList[0];
        log.info("[대기측정소 저장] 유저의 지역명 : {}", region);

        // 1. 지역명으로 해당 지역의 측정소 리스트 뽑아오기
        List<AirStation> stationList = airInfoRepository.findByStationRegionContaining(region);

        while(stationList.isEmpty()){
            log.info("지역으로 못뽑았어 지역에서 한글자 빼보자");
            region = region.substring(0, region.length() - 1);
            log.info("지역 : {}", region);

            if(region.length() == 0)
                break;

            stationList = airInfoRepository.findByStationRegionContaining(region);
        }

        if(stationList.isEmpty() || addressList.length == 1) {
            log.error("[대기측정소 저장 임의 에러 처리로 측정소 강남으로 저장");
            loginUser.updateAirStation("강남구");
            userRepository.save(loginUser);

            return "강남구";
        }

        AirStation station = null;

        log.info("내 주소 : {}", addressList[1]);
        // 2. 지역에 따라 체크한다
        for(AirStation airStation : stationList) {
            log.info("해당 지역의 측정소 중 지역세부 ; {}", airStation.getRegionDetail());
            if(airStation.getRegionDetail().contains(addressList[1]) || addressList[1].contains(airStation.getRegionDetail())) {
                station = airStation;
                break;
            }
        }

        if(station == null) {
            log.error("[대기측정소 저장] 측정소를 못찾았어.. ");
            station = airInfoRepository.selectOneByRegion(region);
        }

        log.info("[대기측정소 저장] 측정소 찾았어. {} ", station.getStationName());
        loginUser.updateAirStation(station.getStationName());
        userRepository.save(loginUser);

        log.info("[대기측정소 저장] 유저한테 저장했어.");

        return station.getStationName();
    }

    /**
     * 로그인 유저를 반환
     */
    private Users getLoginUser(String token) {
//        String email = jwtUtil.getLoginEmail(token);
        Long userId = jwtUtil.getUserNo();

//        Users loginUser = userRepository.findByEmail(email)
//                .orElseThrow(() -> {
//                    log.error("[User Service] 로그인 유저를 찾을 수 없습니다.");
//                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage());
//                });

        Users loginUser = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("[User Service] 로그인 유저를 찾을 수 없습니다.");
                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage());
                });

        return loginUser;
    }

    @Transactional
    @Override
    public void logout(String token) {
        // 로그아웃 여부를 redis에 넣어서 accessToken이 유효한지 체크
        String email = jwtUtil.getLoginEmail(token);
        log.info("[로그아웃] 로그아웃 요청 email : {}", email);

        // 에러 추가(403)? --> 노션도 수정하기


        long remainMilliSeconds = jwtUtil.getRemainMilliSeconds(token);
        refreshTokenRepository.deleteById(email);
        logoutAccessTokenRepository.save(LogoutAccessToken.builder()
                .email(email)
                .accessToken(token)
                .expiration(remainMilliSeconds / 1000)
                .build());

        log.info("[로그아웃] 로그아웃 처리 완료!");
    }

    /**
     * 비밀번호 찾기를 위한 이메일 & 닉네임 일치 체크
     * 이메일과 닉네임 모두 동일해야 true / 하나라도 틀리면 false를 반환합니다.
     * true가 반환되면 EmailService의 sendEmail이 실행됩니다.
     *
     * 존재하지 않는 유저일때만 에러를 반환합니다.
     * @param findPasswordReqDto
     * @return true / false
     */
    @Transactional
    @Override
    public Boolean findPassword(FindPasswordReqDto findPasswordReqDto) {
        String email = findPasswordReqDto.getEmail();
        String nickname = findPasswordReqDto.getNickname();
        log.info("[비밀번호 찾기] 찾기 요청. email : {}, nickname : {}", email, nickname);

        // 우선 존재하는 회원인지 체크
        // 존재하지 않으면 404에러 던짐
        Users findUser = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[비밀번호 찾기] 존재하지 않는 회원입니다.");
                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage());
                });

        // 유저 닉네임 일치 확인
        if(!findUser.getNickname().equals(nickname)) {
            log.error("[비밀번호 찾기] 정보가 일치하지 않습니다.");
            return false;
        }

        log.info("[비밀번호 찾기] 이메일 & 닉네임 모두 일치 !! ");

        return true;
        // 일치하면(true반환) 메일 발송 메서드 실행 ---> 프론트

        // 코드 일치 확인할 필요없음
        // 받은 비번으로 지들이 로그인 할거니까
    }

    /**
     * 임시 비밀번호를 발급합니다.
     */
    @Transactional
    @Override
    public void updateTempPassword(String email, String tempPassword) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[임시 비밀번호 발급] 존재하지 않는 회원입니다.");
                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage());
                });

        // 임시 패스워드 암호화
        String encodingPassword = passwordEncoder.encode(tempPassword);
        user.updatePassword(encodingPassword);
        userRepository.save(user);
    }


    /**
     * 서비스 이전에 비밀번호 확인을 합니다.
     * 일치하면 true / 불일치하면 false
     * @param token
     * @return
     */
    @Transactional
    @Override
    public Boolean checkPassword(PasswordReqDto reqDto, String token) {
        Users loginUser = getLoginUser(token);
        log.info("[비밀번호 확인] email : {}", loginUser.getEmail());

        if(passwordEncoder.matches(reqDto.getPassword(), loginUser.getPassword())) {
            log.info("[비밀번호 확인] 비밀번호 일치! true 반환.");
            return true;
        }

        log.info("[비밀번호 확인] 비밀번호 불일치! false 반환.");
        return false;
    }

    @Transactional
    @Override
    public void updatePassword(UpdatePasswordReqDto updatePasswordRepDto, String token) {
        // 유저 반환
        Users loginUser = getLoginUser(token);
        log.info("[비밀번호 변경] 변경 요청. 로그인 유저 : {}", loginUser.getEmail());

        String originPassword = loginUser.getPassword();
        // 패스워드 일치 확인
        if(!passwordEncoder.matches(updatePasswordRepDto.getPassword(), originPassword)) {
            log.error("[비밀번호 변경] 비밀번호 불일치! 변경 불가.");
            throw new UserException(UserExceptionMessage.LOGIN_PASSWORD_ERROR.getMessage());
        }

        // 패스워드 일치하면 변경 가능
        // 이전이랑 달라야함 ---> 프론트에서 거를건가?
        if(passwordEncoder.matches(updatePasswordRepDto.getNewPassword(), originPassword)) {
            log.error("[비밀번호 변경] 이전 비밀번호와 다른 비밀번호를 입력해야 합니다.");
            throw new UserException(UserExceptionMessage.UPDATE_SAME_PASSWORD_ERROR.getMessage());
        }

        // 확인한 비밀번호와 일치하지 않음
        if(!updatePasswordRepDto.getNewPassword().equals(updatePasswordRepDto.getNewPasswordCheck())) {
            log.error("[비밀번호 변경] 입력한 비밀번호가 일치하지 않습니다.");
            throw new UserException(UserExceptionMessage.UPDATE_PASSWORD_ERROR.getMessage());
        }

        log.info("[비밀번호 변경] 패스워드 변경 완료.");

        // 패스워드 암호화
        String encodingPassword = passwordEncoder.encode(updatePasswordRepDto.getNewPassword());
        loginUser.updatePassword(encodingPassword);
        userRepository.save(loginUser);
    }

    @Transactional
    @Override
    public UserResDto updateUser(UpdateUserReqDto updateUserReqDto, String token) {
        // 유저 뽑아오기
        Users loginUser = getLoginUser(token);
        log.info("[내 정보 수정] 정보 수정 요청. email : {}", loginUser.getEmail());

        // 내정보 수정 전에 비밀번호 체크(checkPassword) 실행 ---> 프론트

        loginUser.updateUser(updateUserReqDto.getNickname(), updateUserReqDto.getAddress(), updateUserReqDto.getGiroCode());
        userRepository.save(loginUser);

        // 사용자의 주소를 이용하여 대기정보 측정소 찾아서 다시 저장하기
        updateAirStation(loginUser.getEmail());

        log.info("[내 정보 수정] 정보 수정 완료.");

        return UserResDto.entityToDto(loginUser);
    }

    @Transactional
    @Override
    public void withdraw(String token) {
        // 패스워드 확인 -> 쁘론트

        Users loginUser = getLoginUser(token);
        log.info("[회원탈퇴] 회원 탈퇴 요청. email : {}", loginUser.getEmail());

        userRepository.delete(loginUser);
        log.info("[회원탈퇴] 회원 탈퇴 완료. 메서드 종료!");
    }

    @Transactional
    @Override
    public UserResDto findMyInfo(String token) {
        Users loginUser = getLoginUser(token);
        log.info("[내 정보 조회] 정보 조회 요청. email : {}", loginUser.getEmail());

        return UserResDto.entityToDto(loginUser);
    }

    /**
     * Bearer 떼고 엑세스 토큰 가져옴
     */
    private String noPrefixToken(String token) {
        return token.substring(7);
    }

    @Transactional
    @Override
    public ReissueTokenResDto reissueToken(String refreshToken) {
        log.info("[토큰 재발급] 토큰 재발급 요청. refreshToken : {}", refreshToken);
        refreshToken = noPrefixToken(refreshToken);

        // refreshToken에서 email 가져오기
        String email = null;

        try {
            email = jwtUtil.getLoginEmail(refreshToken);
        } catch (Exception e) {
            // 리프레시 토큰 만료
            log.error("[토큰 재발급] 리프레시 토큰이 만료되었습니다. 재로그인 해주세요.");
            throw new TokenException("리프레시 토큰 만료. 재로그인 필수.");
        }

        // refreshToken을 redis 레포에서 가져와서 일치 검사
        String originRefreshToken = refreshTokenRepository.findById(email)
                .orElseThrow(() -> {
                    log.error("[토큰 재발급] 해당 이메일에 대한 토큰이 존재하지 않습니다.");
                    return new NotFoundException("해당 이메일에 대한 토큰 미존재.");
                }).getRefreshToken();

        if(!originRefreshToken.equals(refreshToken)) {
            log.error("[토큰 재발급] 토큰이 일치하지 않아 재발급 불가.");
            log.error("[토큰 재발급] 현재 : {}, 기존 : {}", refreshToken, originRefreshToken);

            throw new TokenException("토큰 불일치.");
        }

        // access & refresh Token 재발급
        String accessToken = jwtUtil.generateAccessToken(email);
        refreshToken = jwtUtil.generateRefreshToken(email);

        // redis에 refreshToken 저장 필요
        // 회원의 이메일 아이디를 키로 저장

        // 기존에 저장된 리프레시 토큰 삭제
        refreshTokenRepository.deleteById(email);

        refreshTokenRepository.save(RefreshToken.builder()
                .email(email)
                .refreshToken(refreshToken)
                .expiration(JwtExpirationEnum.REFRESH_TOKEN_EXPIRATION_TIME.getValue() / 1000)
                .build());

        return ReissueTokenResDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    @Override
    public void deleteUser(Long userId, String token) {
        log.info("[관리자 전용 - 회원 강제탈퇴] 강제탈퇴 요청. token : {}", token);

        // 관리자 체크
        if(!jwtUtil.checkAdmin()) {
            log.error("[관리자 전용 - 회원 강제탈퇴] 관리자 회원만 접근이 가능합니다.");
            throw new UserUnAuthorizedException(UserExceptionMessage.NO_ADMIN_USER.getMessage());
        }

        log.info("[관리자 전용 - 회원 강제탈퇴] 관리자 체크 완료. 해당 회원을 찾아냅니다.. findUserId : {}", userId);
        Users findUser = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("[관리자 전용 - 회원 강제탈퇴] 회원을 찾을 수 없습니다.");
                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage());
                });

        log.info("[관리자 전용 - 회원 강제탈퇴] 회원 찾기 완료! findUserEmail : {}", findUser.getEmail());

        userRepository.delete(findUser);
        log.info("[관리자 전용 - 회원 강제탈퇴] 회원 강제탈퇴 처리 완료. 메서드 종료!");
    }

    @Transactional
    @Override
    public List<AdminUserResDto> findUserList(String token) {
        log.info("[관리자 전용 - 전체 회원 목록 조회] 조회 요청. token : {}", token);

        // 관리자 체크 
        if(!jwtUtil.checkAdmin()) {
            log.error("[관리자 전용 - 전체 회원 목록 조회] 관리자 회원만 접근이 가능합니다.");
            throw new UserUnAuthorizedException(UserExceptionMessage.NO_ADMIN_USER.getMessage());
        }

        log.info("[관리자 전용 - 전체 회원 목록 조회] 관리자 체크 완료. 전체 회원을 조회합니다...");

        return userRepository.findAll().stream()
                .map(user -> AdminUserResDto.entityToDto(user))
                .collect(Collectors.toList());
    }

    // 유저 - 소셜 / 기본 나눠서 조회하거나 이름으로 조히ㅗ하거나 ... 등등
    // 회원 검색 따로 있어야함
    @Transactional
    @Override
    public List<AdminUserResDto> findUserByCondition(UserSearchCondition condition, String token) { // 테스트 해봐야함
        log.info("[관리자 전용 - 회원 검색] 회원 검색 요청. token : {}", token);

        // 관리자 체크
        if(!jwtUtil.checkAdmin()) {
            log.error("[관리자 전용 - 회원 검색] 관리자 회원만 접근이 가능합니다.");
            throw new UserUnAuthorizedException(UserExceptionMessage.NO_ADMIN_USER.getMessage());
        }
        
        log.info("[관리자 전용 - 회원 검색] 검색 완료.");
        return userQueryDslRepository.findByCondition(condition).stream()
                .map(users -> AdminUserResDto.entityToDto(users))
                .collect(Collectors.toList());
    }

    @Override
    public List<FindPowerUsageHouseAvgResDto> findPowerUsageHouseAvgList(Integer year, Integer month) {
        log.info("[회원의 주소 기반 가구평균 전력 사용량 조회] 조회 요청.");
        long userId = jwtUtil.getUserNo();
        String address = userRepository.findById(userId).get().getAddress();
        StringTokenizer st = new StringTokenizer(address);
        String metro = st.nextToken();
        String city = st.nextToken();
        String city2 = st.nextToken();
        log.info("metro----> {}", metro);
        log.info("city----> {}", city);
        if(city.charAt(city.length()-1) == '시') {
            city = city + " " + city2;
        }
        Optional<CityCode> cityCode = cityCodeRepository.findByMetroNameAndCityName(MetroName.getConverted(metro), city);
        if(!cityCode.isPresent()) throw new UserNotFoundException(UserExceptionMessage.ADDRESS_NOT_FOUND.getMessage());
        List<FindPowerUsageHouseAvgResDto> dtoList = new ArrayList<>();
        // 전 달 계산
        LocalDate last = null;
        // 년월으로 date
        LocalDate date = LocalDate.of(year, month, 1);
        FindPowerUsageHouseAvgResDto dto;
        float tmpPowerUsage = 0F;
        int tmpBill = 0;
        for (int i = 4; i >= 0; i--) {
            last = date.minusMonths(i);
            if(last.getYear() == 2023 && last.getMonthValue() >= 9) {
                dto = FindPowerUsageHouseAvgResDto.builder()
                        .year(last.getYear())
                        .month(last.getMonthValue())
                        .powerUsage(null)
                        .bill(null)
                        .build();
            }else {
                dto = kepcoAPIWebClient.findPowerUsageHouseAvg(last.getYear(), last.getMonthValue(), cityCode.get().getMetroCode().getMetroCode(), cityCode.get().getCityCode());
                if (tmpBill == 0 && dto.getBill() != null) {
                    tmpPowerUsage = dto.getPowerUsage();
                    tmpBill = dto.getBill();
                }
            }
            dtoList.add(dto);
        }
        // 2023년 9, 10, 11월 데이터가 없음,,,
        for (int i = 0; i <= 4; i++) {
            if(dtoList.get(i).getPowerUsage() == null) {
                dtoList.get(i).setPowerUsage(tmpPowerUsage + tmpPowerUsage / dtoList.get(i).getMonth());
                dtoList.get(i).setBill(tmpBill + tmpBill / dtoList.get(i).getYear());
            }
        }
        log.info("[회원의 주소 기반 가구평균 전력 사용량 조회] 조회 성공.");
        return dtoList;
    }

    @Transactional
    @Override
    public AdminUserResDto findUser(Long userId, String token) {
        log.info("[관리자 전용 - 회원 조회] 조회 요청. token : {}", token);

        // 관리자 체크 
        if(!jwtUtil.checkAdmin()) {
            log.error("[관리자 전용 - 회원 조회] 관리자 회원만 접근이 가능합니다.");
            throw new UserUnAuthorizedException(UserExceptionMessage.NO_ADMIN_USER.getMessage());
        }

        log.info("[관리자 전용 - 회원 조회] 관리자 체크 완료. 회원을 찾아냅니다... findUserId : {}", userId);
        Users findUser = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("[관리자 전용 - 회원 조회] 회원을 찾을 수 없습니다.");
                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage());
                });

        log.info("[관리자 전용 - 회원 조회] 회원 찾기 완료 ! findUserEmail : {}", findUser.getEmail());

        return AdminUserResDto.entityToDto(findUser);
    }

    @Transactional
    @Override
    public void addGiroCode(GiroCodeReqDto giroCodeRepDto, String token) {
        Users loginUser = getLoginUser(token);
        log.info("[지로 코드 등록] 지로 코드 등록 요청. email : {}", loginUser.getEmail());

        loginUser.addGiroCode(giroCodeRepDto.getGiroCode());
        userRepository.save(loginUser);

        log.info("[지로 코드 등록] 지로 코드 등록 완료. 메서드 종료!");
    }

}
