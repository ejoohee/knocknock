package com.knocknock.domain.category.dao;

import com.knocknock.domain.category.domain.Category;
import com.knocknock.domain.category.domain.CategorySummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<CategorySummary> findAllProjectedBy();

}
