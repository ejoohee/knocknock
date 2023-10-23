package com.knocknock.domain.model.service;

import com.knocknock.domain.model.dao.ModelRepository;
import com.knocknock.domain.model.dto.response.FindModelListReqDto;
import com.knocknock.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    private final ModelRepository modelRepository;
    private final JwtUtil jwtUtil;

    // 목록 조회
    @Override
    public List<FindModelListReqDto> findModelList(String type, String keyword, String category) {
        // 현재 로그인한 회원의 user 기본키 가져오기
        Long userId = jwtUtil.getUserNo();
        return modelRepository.findModelList(userId, type, keyword, category);
    }


}
