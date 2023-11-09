package com.knocknock.domain.model.service;

import com.knocknock.domain.model.constant.AwsS3ImgLink;
import com.knocknock.domain.model.dao.ModelRepository;
import com.knocknock.domain.model.dao.MyModelRepository;
import com.knocknock.domain.model.domain.Model;
import com.knocknock.domain.model.domain.MyModel;
import com.knocknock.domain.model.dto.request.AddMyModelReqDto;
import com.knocknock.domain.model.dto.response.FindMyModelListResDto;
import com.knocknock.domain.model.dto.response.FindMyModelResDto;
import com.knocknock.domain.model.exception.ModelNotFoundException;
import com.knocknock.domain.user.dao.UserRepository;
import com.knocknock.domain.user.domain.Users;
import com.knocknock.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MyModelServiceImpl implements MyModelService {

    private final MyModelRepository myModelRepository;
    private final ModelRepository modelRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;


    @Override
    public void addMyModel(AddMyModelReqDto addMyModelReqDto) {
        log.info("[내 가전제품 등록] 내 가전제품 등록 요청.");

        // 현재 로그인한 회원의 user 기본키 가져오기
        Long userId = jwtUtil.getUserNo();
        log.info("[가전제품 찜 등록] 현재 로그인한 회원의 userId -----> {}", userId);
        // user 객체 select문 호출을 하지 않기 위함
        Users user = userRepository.getReferenceById(userId);
        // 모델명으로 model 조회 없으면 404
        Model model = modelRepository.findModelByName(addMyModelReqDto.getModelName()).orElseThrow(() -> {
            log.error("[내 가전제품 등록] 조회 실패...해당하는 가전제품이 존재하지 않습니다.");
            return new ModelNotFoundException("해당하는 가전제품이 존재하지 않습니다.");
        });
        MyModel myModel = MyModel.builder()
                .user(user)
                .model(model)
                .modelNickname(addMyModelReqDto.getModelNickname())
                .build();

        myModelRepository.save(myModel);
        log.info("[내 가전제품 등록] 내 가전제품 등록 성공.");
    }

    @Override
    public void deleteMyModel(long modelId) {
        log.info("[내 가전제품 삭제] 내 가전제품 삭제 요청.");
        // 현재 로그인한 회원의 user 기본키 가져오기
        Long userId = jwtUtil.getUserNo();
        log.info("[내 가전제품 삭제] 현재 로그인한 회원의 userId -----> {}", userId);
        log.info("[내 가전제품 삭제] 삭제하려는 modelId -----> {}", modelId);
        myModelRepository.deleteByUserAndModel(userId, modelId);

        log.info("[내 가전제품 삭제] 내 가전제품 삭제 성공.");

    }

    @Override
    @Transactional(readOnly = true)
    public List<FindMyModelListResDto> findMyModelList(String category) {
        log.info("[내 가전제품 목록 조회] 내 가전제품 목록 조회 요청.");

        // 현재 로그인한 회원의 user 기본키 가져오기
        Long userId = jwtUtil.getUserNo();
        log.info("[내 가전제품 목록 조회] 현재 로그인한 회원의 userId -----> {}", userId);
        return myModelRepository.findMyModelList(userId, category);
    }

    @Override
    @Transactional(readOnly = true)
    public FindMyModelResDto findMyModel(long myModelId) {
        log.info("[내 가전제품 상세 정보 조회] 내 가전제품 상세 정보 조회 요청.");
        MyModel myModel = myModelRepository.findById(myModelId).orElseThrow(() -> {
            log.error("[내 가전제품 상세 정보 조회] 조회 실패...내가 등록한 가전제품에 존재하지 않는 가전제품입니다.");
            return new ModelNotFoundException("내가 등록한 가전제품에 존재하지 않는 가전제품입니다.");
        });
        Model model = modelRepository.findModelById(myModel.getModel().getId()).orElseThrow(() -> {
            log.error("[내 가전제품 상세 정보 조회] 조회 실패...해당하는 가전제품이 존재하지 않습니다.");
            return new ModelNotFoundException("해당하는 가전제품이 존재하지 않습니다.");
        });

        log.info("[내 가전제품 상세 정보 조회] 내 가전제품 상세 정보 조회 성공.");
        return FindMyModelResDto.builder()
                .myModelId(myModelId)
                .modelId(model.getId())
                .category(model.getCategory().getName())
                .modelNickname(myModel.getModelNickname())
                .modelName(model.getName())
                .modelBrand(model.getBrand())
                .modelGrade(model.getGrade())
                .modelImg(model.getImg() == null ? null : AwsS3ImgLink.getLink(model.getName()))
                .usage1(model.getCategory().getUsage1())
                .usageValue1(model.getUsageValue1())
                .usageUnit1(model.getCategory().getUsageUnit1())
                .usage2(model.getCategory().getUsage2())
                .usageValue2(model.getUsageValue2())
                .usageUnit2(model.getCategory().getUsageUnit2())
                .usage3(model.getCategory().getUsage3())
                .usageValue3(model.getUsageValue3())
                .usageUnit3(model.getCategory().getUsageUnit3())
                .modelCo2(model.getCo2())
                .co2Unit(model.getCategory().getCo2Unit())
                .modelCost(model.getCost())
                .costUnit(model.getCategory().getCostUnit())
//                .releasedDate(model.getReleasedDate())
                // null이 아니면 문자열로 변환해서 반환
                .addAtPin((myModel.getAddAtPin() == null) ? null : myModel.getAddAtPin().toString())
                .build();
    }

    @Override
    public void updateMyModelPinned(long myModelId) {
        log.info("[내 가전제품 핀 상태 갱신] 내 가전제품 핀 상태 갱신 요청.");

        MyModel myModel = myModelRepository.findById(myModelId).orElseThrow(() -> {
            log.error("[내 가전제품 핀 상태 갱신] 내가 등록한 가전제품에 존재하지 않는 가전제품입니다.");
            return new ModelNotFoundException("내가 등록한 가전제품에 존재하지 않는 가전제품입니다.");
        });

        // 핀 등록한 날짜 기입
        Date now = null;
        if(myModel.getAddAtPin() == null) now = new Date();
        myModel.setAddAtPin(now);
        log.info("[내 가전제품 핀 상태 갱신] 내 가전제품 핀 상태 갱신 성공.");
    }


}
