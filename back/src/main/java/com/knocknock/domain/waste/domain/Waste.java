package com.knocknock.domain.waste.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Waste {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer wasteId;

    @Column(name = "ENTRPS_NM")
    private String enterpriseName;

    @Column(name = "ADRES")
    private String address;

    @Column(name = "TELNO")
    private String telNo;

    @Column(name = "WSTE")
    private String wasteType;

    @Column(name = "X")
    private Double x;

    @Column(name = "y")
    private Double y;

}
