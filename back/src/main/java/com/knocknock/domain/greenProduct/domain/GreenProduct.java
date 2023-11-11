package com.knocknock.domain.greenProduct.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class GreenProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long greenProductId;
    //코드
    private String prodIxid;
    //제품명
    private String prodMdel;
    //회사명
    private String prodVcnm;
    //상세이유
    private String prodInrs;
    //인증시작일
    private String prodRsdt;
    //인증만료일
    private String prodRedt;
    //인증, 비인증
    private String prodRsnm;

    @Builder
    public GreenProduct(String prodIxid, String prodMdel, String prodVcnm, String prodInrs, String prodRsdt, String prodRedt, String prodRsnm) {
        this.prodIxid = prodIxid;
        this.prodMdel = prodMdel;
        this.prodVcnm = prodVcnm;
        this.prodInrs = prodInrs;
        this.prodRsdt = prodRsdt;
        this.prodRedt = prodRedt;
        this.prodRsnm = prodRsnm;
    }

    public void updateProdRsnm(String newProdRsnm) {
        this.prodRsnm = newProdRsnm;
    }
}
