package com.backend.allreva.module.recruitment.rent.application.dto;

import com.backend.allreva.module.member.domain.value.RefundAccount;
import com.backend.allreva.module.recruitment.rent.domain.value.BusSize;
import com.backend.allreva.module.recruitment.rent.domain.value.BusType;
import com.backend.allreva.module.recruitment.rent.domain.value.RefundType;
import com.backend.allreva.module.recruitment.rent.domain.value.Region;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
public class RentDetailResponse {

    private final String concertName;
    private final String imageUrl;
    private final String title;
    private final String artistName;
    private final Region region;
    private final String boardingArea;
    private final String dropOffArea;
    private final String upTime;
    private final String downTime;
    @Setter
    private List<RentBoardingDateResponse> boardingDates;
    private final BusSize busSize;
    private final BusType busType;
    private final int maxPassenger;
    private final int roundPrice;
    private final int upTimePrice;
    private final int downTimePrice;
    private final int recruitmentCount;
    private final LocalDate endDate;
    private final String chatUrl;
    private final RefundType refundType;
    private final String information;
    private final boolean isClosed;
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RefundAccount refundAccount;

    public RentDetailResponse(
            final String concertName,
            final String imageUrl,
            final String title,
            final String artistName,
            final Region region,
            final String boardingArea,
            final String dropOffArea,
            final String upTime,
            final String downTime,
            final List<RentBoardingDateResponse> boardingDates,
            final BusSize busSize,
            final BusType busType,
            final int maxPassenger,
            final int roundPrice,
            final int upTimePrice,
            final int downTimePrice,
            final int recruitmentCount,
            final LocalDate endDate,
            final String chatUrl,
            final RefundType refundType,
            final String information,
            final boolean isClosed
    ) {
        this.concertName = concertName;
        this.imageUrl = imageUrl;
        this.title = title;
        this.artistName = artistName;
        this.region = region;
        this.boardingArea = boardingArea;
        this.dropOffArea = dropOffArea;
        this.upTime = upTime;
        this.downTime = downTime;
        this.boardingDates = boardingDates;
        this.busSize = busSize;
        this.busType = busType;
        this.maxPassenger = maxPassenger;
        this.roundPrice = roundPrice;
        this.upTimePrice = upTimePrice;
        this.downTimePrice = downTimePrice;
        this.recruitmentCount = recruitmentCount;
        this.endDate = endDate;
        this.chatUrl = chatUrl;
        this.refundType = refundType;
        this.information = information;
        this.isClosed = isClosed;
    }

    @Getter
    public static class RentBoardingDateResponse {
        private final LocalDate date;
        private final int participationCount;
        @Setter
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean isApplied;

        public RentBoardingDateResponse(
                final LocalDate date,
                final int participationCount
        ) {
            this.date = date;
            this.participationCount = participationCount;
        }
    }
}
