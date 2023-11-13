package com.knocknock.domain.model.service;

import com.knocknock.domain.model.constant.AwsS3ImgLink;
import com.knocknock.domain.model.dao.LikeModelRepository;
import com.knocknock.domain.model.dao.ModelRepository;
import com.knocknock.domain.model.dao.MyModelRepository;
import com.knocknock.domain.model.domain.Model;
import com.knocknock.domain.model.domain.MyModel;
import com.knocknock.domain.model.dto.request.CheckModelByLabelImgReqDto;
import com.knocknock.domain.model.dto.response.CheckModelResDto;
import com.knocknock.domain.model.dto.response.CompareModelAndMyModelResDto;
import com.knocknock.domain.model.dto.response.FindModelListResDto;
import com.knocknock.domain.model.dto.response.FindModelResDto;
import com.knocknock.domain.model.exception.ModelNotFoundException;
import com.knocknock.global.common.ocr.OCRAPIWebClient;
import com.knocknock.global.common.openapi.OpenAPIWebClient;
import com.knocknock.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ModelServiceImpl implements ModelService {

    private final ModelRepository modelRepository;
    private final MyModelRepository myModelRepository;
    private final LikeModelRepository likeModelRepository;
    private final JwtUtil jwtUtil;
    private final OpenAPIWebClient openAPIWebClient;
    private final OCRAPIWebClient ocrapiWebClient;

    // 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<FindModelListResDto> findModelList(String type, String keyword, String category) {
        // 현재 로그인한 회원의 user 기본키 가져오기
        Long userId = jwtUtil.getUserNo();
        log.info("[가전제품 목록 조회] 현재 로그인한 회원의 기본키 {}", userId);
        log.info("type-->{}", type);
        log.info("keyword-->{}", keyword);
        if(category == null) log.info("category null");
        else log.info("category is not null");
        List<FindModelListResDto> findModelListResDtoList = modelRepository.findModelList(userId, type, keyword, category);
        // 찜 되어있는지
        for (FindModelListResDto modelDto : findModelListResDtoList) {
            // 찜한 상품
            modelDto.setIsLiked(isLiked(userId, modelDto.getModelId()));
        }
        log.info("[가전제품 목록 조회] 가전제품 목록 조회 성공.");
        return findModelListResDtoList;
    }

    // 찜 여부 반환
    private boolean isLiked(long userId, long modelId) {
        return likeModelRepository.findLikeModelByUserAndModel(userId, modelId).isPresent();
    }

    // 상세 정보 조회
    @Override
    @Transactional(readOnly = true)
    public FindModelResDto findModel(long modelId) {
        Model model = modelRepository.findModelById(modelId).orElseThrow(() -> {
            log.error("[가전제품 상세 정보 조회] 조회 실패...해당하는 가전제품이 존재하지 않습니다.");
            return new ModelNotFoundException("해당하는 가전제품이 존재하지 않습니다.");
        });
        // 찜한 가전제품 인지 확인
        // 현재 로그인한 회원의 user 기본키 가져오기
        Long userId = jwtUtil.getUserNo();

        log.info("[가전제품 상세 정보 조회] 가전제품 상세 정보 조회 성공.");
        return FindModelResDto.builder()
                .modelId(modelId)
                .category(model.getCategory().getName())
                .modelName(model.getName())
                .modelBrand(model.getBrand())
                .modelGrade(model.getGrade())
                .modelImg(model.getImg() == null ? null : AwsS3ImgLink.getLink(model.getName()))
                .modelURL(model.getUrl())
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
                .isLiked(isLiked(userId, modelId))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CheckModelResDto checkModelByModelName(String modelName) {
        // 현재 로그인한 회원의 user 기본키 가져오기
        Long userId = jwtUtil.getUserNo();
        // 내 가전제품에 이미 등록되었는지 검사
        if(myModelRepository.findByUserAndModel(userId, modelName).isPresent()){
            log.error("[가전제품 모델명으로 조회] 내 가전제품에 이미 등록된 가전제품입니다.");
            throw new IllegalArgumentException("내 가전제품에 이미 등록된 가전제품입니다.");
        }

        CheckModelResDto checkModelResDto = modelRepository.checkModelByModelName(modelName);
        if(checkModelResDto == null) {
            log.error("[가전제품 모델명으로 조회] 조회 실패...해당하는 가전제품이 존재하지 않습니다.");
            throw new ModelNotFoundException("해당하는 가전제품이 존재하지 않습니다.");
        }

        checkModelResDto.setModelImg(checkModelResDto.getModelImg() == null ? null : AwsS3ImgLink.getLink(checkModelResDto.getModelName()));
        log.info("[가전제품 모델명으로 조회] 가전제품 모델명으로 조회 성공.");
        return checkModelResDto;
    }

    @Override
    public CompareModelAndMyModelResDto compareModelAndMyModel(long modelId, long myModelId) {
        log.info("[가전제품 비교] 가전제품 비교 요청.");
        CompareModelAndMyModelResDto dto = new CompareModelAndMyModelResDto();
        // 가전제품과 내 가전제품을 비교한다,,,?
        // 비교군
        Model model1 = modelRepository.findModelById(modelId).orElseThrow(); // 비교 하려는 가전제품
        MyModel myModel = myModelRepository.findById(myModelId).orElseThrow(() -> {
            log.error("[내 가전제품과 다른 가전제품 비교] 조회 실패...내가 등록한 가전제품에 존재하지 않는 가전제품입니다.");
            return new ModelNotFoundException("내가 등록한 가전제품에 존재하지 않는 가전제품입니다.");
        });

        Model model2 = modelRepository.findModelById(myModel.getModel().getId()).orElseThrow(() -> {
            log.error("[내 가전제품과 다른 가전제품 비교] 조회 실패...해당하는 가전제품이 존재하지 않습니다.");
            return new ModelNotFoundException("해당하는 가전제품이 존재하지 않습니다.");
        }); // 비교 당하는 내 가전제품

        dto.setModelAName(model1.getName());
        dto.setModelBName(myModel.getModelNickname());

        // 1. 에너지 효율 등급
        dto.setModelAGrade(model1.getGrade());
        dto.setModelBGrade(model2.getGrade());
        //        if(model1.getGrade() > model2. getGrade()) {
//            // 에너지 효율 등급이 높으면
//
//            // 에너지 효율 등급이 같으면
//
//            // 에너지 효율 등급이 낮으면
//
//        }else if(model1.getGrade() == model2.getGrade()) {
//
//
//        }else {
//
//        }
        // 2. 연간 에너지 비용
        dto.setModelACost(model1.getCost());
        dto.setModelBCost(model2.getCost());

        // 연간에너지 비용이 더 큰지
        // 같은지
        // 작은지

        // 3. co2 비용
        // 연 단위
        dto.setModelACo2((long) (model1.getCo2() * 24 * 365));

        dto.setModelBCo2((long) (model2.getCo2() * 24 * 365));
        // 더 큰지
        // 같은지
        // 더 작은지


        // 4. 각 항목(최대 3가지)
        //
        log.info("[가전제품 비교] 가전제품 비교 성공.");
        return dto;
    }

    @Override
    public CheckModelResDto checkModelByLabelImg(CheckModelByLabelImgReqDto checkModelByLabelImgReqDto) {
        log.info("[가전제품 모델명 라벨 이미지에서 확인] 가전제품 모델명 라벨 이미지에서 확인.");
        // ocr api 호출
        // inferText 배열 받아오기
        List<String> textList = ocrapiWebClient.getTextListFromImg(checkModelByLabelImgReqDto.getLabelImg());
        // 모델명에 해당하는 값을 db 조회로 확인?
        String modelName = null;
        for (String text : textList) {
            // 디비에 있는 모델명 최소길이가 8
            if(text.length() < 8) continue;
            // 영어로 시작해야 함
            if(!checkPattern(text)) continue;
            if(modelRepository.findModelByName(text).isPresent()) {
                modelName = text;
                break;
            }
        }

        if(modelName == null) {
            log.error("[가전제품 모델명 라벨 이미지에서 확인] 모델명 인식 불가능!!!!!!!!!");
            throw new ModelNotFoundException("사진에서 모델명을 인식할 수 없습니다.");
        }

        log.info("[가전제품 모델명 라벨 이미지에서 확인] 가전제품 모델명 라벨 이미지에서 확인 성공------->{}", modelName);
        return checkModelByModelName(modelName);
    }

    // 정규표현식으로 모델명 패턴에 해당되는지 검사
    private boolean checkPattern(String s) {
        String regex = "^[A-Za-z][A-Za-z0-9]*$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);

        return matcher.matches();
    }

    //    @Scheduled(cron = "")
    public void addModel() {
        // 기기 제품 정보 api 호출해서 모델명 가져와서,, 정보 가져와서,,,,
        try {
            List<Model> modelList = openAPIWebClient.findModelList();
            modelRepository.saveAll(modelList);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
