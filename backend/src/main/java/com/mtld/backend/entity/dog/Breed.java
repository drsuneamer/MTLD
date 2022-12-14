package com.mtld.backend.entity.dog;

import com.mtld.backend.entity.BaseEntity;
import com.nimbusds.openid.connect.sdk.claims.Gender;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * created by myeongseok on 2022/09/08
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Breed extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String name;

    public Breed(String code, String name) {
        this.code = code;
        this.name = name;
    }
}

