package com.knocknock.domain.model.dao;

import com.knocknock.domain.model.domain.MyModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyModelRepository extends JpaRepository<MyModel, Long> {


}
