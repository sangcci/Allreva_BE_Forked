package com.backend.allreva.concert.concert.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ConcertInfo {

    private String title;
    private String price;
    private ConcertStatus performStatus;
    private String host;
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
