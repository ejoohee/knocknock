package com.knocknock.domain.greenProduct.dao;

import com.knocknock.domain.greenProduct.domain.GreenProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GreenProductRepository extends JpaRepository<GreenProduct, Long> {
    List<GreenProduct> findByProdMdelContainingOrProdVcnmContaining(String productName, String companyName);

    GreenProduct findByProdIxid(String prodIxid);

}
