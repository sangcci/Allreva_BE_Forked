package com.backend.allreva.module.concert.concert.domain.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ConcertInfo {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String price;

    @Column(nullable = false, name = "prfstate")
    @Enumerated(EnumType.STRING)
    private ConcertStatus performStatus;

    private String host;

    @Embedded
    private DateInfo dateInfo;

    @Builder
    private ConcertInfo(
            final String title,
            final String price,
            final ConcertStatus performStatus,
            final String host,
            final DateInfo dateInfo) {
        this.title = title;
        this.price = price;
        this.performStatus = performStatus;
        this.host = host;
        this.dateInfo = dateInfo;
    }
}
