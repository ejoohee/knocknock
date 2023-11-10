package com.knocknock.domain.user.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "metrocode")
public class MetroCode {

    @Id
    @Column(name = "metro_code")
    private Integer metroCode;

    @Column(name = "metro_name", nullable = false)
    private String metroName;

}
