package com.backend.allreva.module.recruitment.rent.domain.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Price {

    @Column(nullable = false)
    private int roundPrice;

    @Column(nullable = false)
    private int upTimePrice;

    @Column(nullable = false)
    private int downTimePrice;
}
