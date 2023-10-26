package com.knocknock.domain.model.service;


import com.knocknock.domain.model.dao.LikeModelRepository;
import com.knocknock.domain.model.dao.ModelRepository;
import com.knocknock.domain.model.domain.LikeModel;
import com.knocknock.domain.model.domain.Model;
import com.knocknock.domain.model.dto.response.FindLikeModelListResDto;
import com.knocknock.domain.model.exception.ModelNotFoundException;
import com.knocknock.domain.user.dao.UserRepository;
import com.knocknock.domain.user.domain.Users;
import com.knocknock.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LikeModelServiceImpl implements LikeModelService {

    private final LikeModelRepository likeModelRepository;
    private final ModelRepository modelRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void addLikeModel(long modelId) {
        log.info("[가전제품 찜 등록] 가전제품 찜 등록 요청.");
        // 현재 로그인한 회원의 user 기본키 가져오기
        Long userId = jwtUtil.getUserNo();
        log.info("[가전제품 찜 등록] 현재 로그인한 회원의 userId -----> {}", userId);
        // 불필요한 select문 방지
        Users user = userRepository.getReferenceById(userId);
        // 불필요한 select문 방지
        Model model = modelRepository.getReferenceById(modelId);
        log.info("[가전제품 찜 등록] 찜하려는 가전제품 modelId -----> {}", modelId);
//        Model model = modelRepository.findModelById(modelId).orElseThrow(() -> new ModelNotFoundException("해당하는 가전제품이 존재하지 않습니다."));
        LikeModel likeModel = LikeModel.builder()
                .model(model)
                .user(user)
                .build();
        likeModelRepository.save(likeModel);
        log.info("[가전제품 찜 등록] 가전제품 찜 등록 성공.");
    }

    @Override
    public void deleteLikeModel(long likeModelId) {
        log.info("[가전제품 찜 취소] 가전제품 찜 취소 요청.");
        log.info("[가전제품 찜 취소] likeModelId -----------> {}", likeModelId);
        likeModelRepository.deleteById(likeModelId);
        log.info("[가전제품 찜 취소] 가전제품 찜 취소 성공.");
    }

    @Override
    @Transactional(readOnly = true)
    public List<FindLikeModelListResDto> findLikeModelList(String category) {
        log.info("[가전제품 찜 목록 조회] 가전제품 찜 목록 조회 요청.");
        // 현재 로그인한 회원의 user 기본키 가져오기
        Long userId = jwtUtil.getUserNo();
        return likeModelRepository.findLikeModelList(userId, category);
    }


}
