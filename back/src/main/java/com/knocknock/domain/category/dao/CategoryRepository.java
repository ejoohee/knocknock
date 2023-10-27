package com.knocknock.domain.category.dao;

import com.knocknock.domain.category.domain.Category;
import com.knocknock.domain.category.domain.CategorySummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<CategorySummary> findAllProjectedBy();

    // 카테고리 이름으로 찾기
    Optional<Category> findByName(String category);

}
