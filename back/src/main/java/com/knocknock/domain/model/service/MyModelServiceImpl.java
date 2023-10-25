package com.knocknock.domain.model.service;

import com.knocknock.domain.model.dao.ModelRepository;
import com.knocknock.domain.model.dao.MyModelRepository;
import com.knocknock.domain.model.domain.Model;
import com.knocknock.domain.model.domain.MyModel;
import com.knocknock.domain.model.dto.request.AddMyModelReqDto;
import com.knocknock.domain.model.dto.response.FindModelResDto;
import com.knocknock.domain.model.dto.response.FindMyModelListResDto;
import com.knocknock.domain.model.dto.response.FindMyModelResDto;
import com.knocknock.domain.model.exception.ModelNotFoundException;
import com.knocknock.domain.user.dao.UserRepository;
import com.knocknock.domain.user.domain.Users;
import com.knocknock.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MyModelServiceImpl implements MyModelService {

    private final MyModelRepository myModelRepository;
    private final ModelRepository modelRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;


    @Override
    public void addMyModel(AddMyModelReqDto addMyModelReqDto) {
        // 현재 로그인한 회원의 user 기본키 가져오기
        Long userId = jwtUtil.getUserNo();
        // user 객체 select문 호출을 하지 않기 위함
        Users user = userRepository.getReferenceById(userId);
        // 모델명으로 model 조회 없으면 404
        Model model = modelRepository.findModelByName(addMyModelReqDto.getModelName()).orElseThrow(() -> new ModelNotFoundException("해당하는 가전제품이 존재하지 않습니다."));
        MyModel myModel = MyModel.builder()
                .user(user)
                .model(model)
                .modelNickname(addMyModelReqDto.getModelNickname())
                .build();
        myModelRepository.save(myModel);
    }

    @Override
    public void deleteMyModel(long modelId) {
        // 현재 로그인한 회원의 user 기본키 가져오기
        Long userId = jwtUtil.getUserNo();
        myModelRepository.deleteByUserAndModel(userId, modelId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FindMyModelListResDto> findMyModelList(String category) {
        // 현재 로그인한 회원의 user 기본키 가져오기
        Long userId = jwtUtil.getUserNo();
        return myModelRepository.findMyModelList(userId, category);
    }

    @Override
    @Transactional(readOnly = true)
    public FindMyModelResDto findMyModel(long myModelId) {
        MyModel myModel = myModelRepository.findById(myModelId).orElseThrow(() -> new ModelNotFoundException("내가 등록한 가전제품에 존재하지 않는 가전제품입니다."));
        Model model = modelRepository.findModelById(myModel.getModel().getId()).orElseThrow(() -> new ModelNotFoundException("해당하는 가전제품이 존재하지 않습니다."));

        return FindMyModelResDto.builder()
                .myModelId(myModelId)
                .modelId(model.getId())
                .category(model.getCategory().getName())
                .modelName(model.getName())
                .modelBrand(model.getBrand())
                .modelGrade(model.getGrade())
                .modelImg(model.getImg())
                .modelURL(model.getUrl())
                .usage(model.getCategory().getUsage())
                .modelUsage(model.getUsage())
                .usageUnit(model.getCategory().getUsageUnit())
                .usage2(model.getCategory().getUsage2())
                .modelUsage2(model.getUsage2())
                .usageUnit2(model.getCategory().getUsageUnit2())
                .usage3(model.getCategory().getUsage3())
                .modelUsage3(model.getUsage3())
                .usageUnit3(model.getCategory().getUsageUnit3())
                .modelCo2(model.getCo2())
                .modelCost(model.getCost())
//                .releasedDate(model.getReleasedDate())
                // null이 아니면 문자열로 변환해서 반환
                .addAtPin((myModel.getAddAtPin() == null) ? null : myModel.getAddAtPin().toString())
                .build();
    }

    @Override
    public void updateMyModelPinned(long myModelId) {
        MyModel myModel = myModelRepository.findById(myModelId).orElseThrow(() -> new ModelNotFoundException("내가 등록한 가전제품에 존재하지 않는 가전제품입니다."));
        // 핀 등록한 날짜 기입
        Date now = null;
        if(myModel.getAddAtPin() == null) now = new Date();
        myModel.setAddAtPin(now);
    }


}
