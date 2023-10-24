package com.knocknock.domain.model.service;

import com.knocknock.domain.model.dao.ModelRepository;
import com.knocknock.domain.model.dao.MyModelRepository;
import com.knocknock.domain.model.domain.Model;
import com.knocknock.domain.model.domain.MyModel;
import com.knocknock.domain.model.dto.request.AddMyModelReqDto;
import com.knocknock.domain.model.exception.ModelNotFoundException;
import com.knocknock.domain.user.dao.UserRepository;
import com.knocknock.domain.user.domain.Users;
import com.knocknock.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
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



}
