package com.knocknock.domain.model.dao;

import com.knocknock.domain.category.domain.Category;
import com.knocknock.domain.model.domain.Model;
import com.knocknock.domain.model.dto.response.FindModelListReqDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ModelRepositoryCustomImplTest {

    @Autowired
    private ModelRepository modelRepository;

    @Test
    void findModelList() {

        List<FindModelListReqDto> findModelListReqDtoList
                = modelRepository.findModelList(1L, "name", "모델", "카테고리2");
        for (FindModelListReqDto findModelListReqDto: findModelListReqDtoList) {
            System.out.println("조회된 가전제품 id" + findModelListReqDto.getModelId());
        }
    }
}