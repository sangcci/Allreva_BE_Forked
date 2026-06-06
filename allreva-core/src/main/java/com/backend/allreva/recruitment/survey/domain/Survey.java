package com.backend.allreva.recruitment.survey.domain;

import com.backend.allreva.common.exception.CustomException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Survey {

    private Long id;
    private String title;
    private List<LocalDate> boardingDates;
    private Region region;
    private LocalDate endDate;
    private int maxPassenger;
    private String information;
    private String concertCode;
    private Long memberId;
    private boolean closed;

    @Builder
    private Survey(
            final Long id,
            final Long memberId,
            final String concertCode,
            final String title,
            final Region region,
            final LocalDate endDate,
            final int maxPassenger,
            final String information,
            final List<LocalDate> boardingDates,
            final boolean closed) {
        this.id = id;
        this.memberId = memberId;
        this.concertCode = concertCode;
        this.title = title;
        this.region = region;
        this.endDate = endDate;
        this.maxPassenger = maxPassenger;
        this.information = information;
        this.boardingDates = boardingDates != null ? new ArrayList<>(boardingDates) : new ArrayList<>();
        this.closed = closed;
    }

    public void update(
            final String title,
            final Region region,
            final LocalDate endDate,
            final int maxPassenger,
            final String information,
            final List<LocalDate> boardingDates) {
        this.title = title;
        this.region = region;
        this.endDate = endDate;
        this.maxPassenger = maxPassenger;
        this.information = information;
        this.boardingDates = boardingDates != null ? new ArrayList<>(boardingDates) : new ArrayList<>();
    }

    public void validateWriter(final Long loginMemberId) {
        if (!this.memberId.equals(loginMemberId)) {
            throw new CustomException(SurveyErrorCode.SURVEY_NOT_WRITER);
        }
    }

    public void validateBoardingDate(final LocalDate boardingDate) {
        boolean missing = this.boardingDates.stream().noneMatch(bd -> bd.equals(boardingDate));
        if (missing) {
            throw new CustomException(SurveyErrorCode.SURVEY_INVALID_BOARDING_DATE);
        }
    }

    public boolean isClosed() {
        return closed;
    }
}
