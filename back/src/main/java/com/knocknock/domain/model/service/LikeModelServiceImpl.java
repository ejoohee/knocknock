package com.knocknock.domain.model.service;


import com.knocknock.domain.model.dao.LikeModelRepository;
import com.knocknock.domain.model.dao.ModelRepository;
import com.knocknock.domain.model.domain.LikeModel;
import com.knocknock.domain.model.domain.Model;
import com.knocknock.domain.model.exception.ModelNotFoundException;
import com.knocknock.domain.user.dao.UserRepository;
import com.knocknock.domain.user.domain.Users;
import com.knocknock.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeModelServiceImpl implements LikeModelService {

    private final LikeModelRepository likeModelRepository;
    private final ModelRepository modelRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void addLikeModel(long modelId) {
        // 현재 로그인한 회원의 user 기본키 가져오기
        Long userId = jwtUtil.getUserNo();
        // 불필요한 select문 방지
        Users user = userRepository.getReferenceById(userId);
        // 불필요한 select문 방지
        Model model = modelRepository.getReferenceById(modelId);
//        Model model = modelRepository.findModelById(modelId).orElseThrow(() -> new ModelNotFoundException("해당하는 가전제품이 존재하지 않습니다."));
        LikeModel likeModel = LikeModel.builder()
                .model(model)
                .user(user)
                .build();
        likeModelRepository.save(likeModel);
    }


}
