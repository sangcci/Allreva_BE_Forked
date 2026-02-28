package com.backend.allreva.module.recruitment.survey.domain;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.model.BaseEntity;
import com.backend.allreva.module.recruitment.survey.domain.value.Region;
import com.backend.allreva.module.recruitment.survey.exception.SurveyErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE survey SET deleted_at = NOW() WHERE id = ?")
@Entity
@Table(name = "survey")
public class Survey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "boarding_dates", columnDefinition = "jsonb", nullable = false)
    private List<LocalDate> boardingDates = new ArrayList<>();

    @Column(nullable = false)
    private String artistName;

    @Column(nullable = false)
    private Region region;

    @Column(nullable = false, name = "eddate")
    private LocalDate endDate;

    @Column(nullable = false)
    private int maxPassenger;

    @Column(nullable = true)
    private String information;

    @Column(nullable = false)
    private Long concertId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private boolean isClosed;

    @Builder
    private Survey(
            final Long memberId,
            final Long concertId,
            final String title,
            final String artistName,
            final Region region,
            final LocalDate endDate,
            final int maxPassenger,
            final String information,
            final List<LocalDate> boardingDates) {
        this.memberId = memberId;
        this.concertId = concertId;
        this.title = title;
        this.artistName = artistName;
        this.region = region;
        this.endDate = endDate;
        this.maxPassenger = maxPassenger;
        this.information = information;
        this.boardingDates = boardingDates != null ? boardingDates : new ArrayList<>();
        this.isClosed = false;
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
        this.boardingDates = boardingDates != null ? boardingDates : new ArrayList<>();
    }

    public void isWriter(final Long loginMemberId) {
        if (!this.memberId.equals(loginMemberId)) {
            throw new CustomException(SurveyErrorCode.SURVEY_NOT_WRITER);
        }
    }

    public void containsBoardingDate(final LocalDate boardingDate) {
        boolean contain = this.boardingDates.stream().noneMatch(bd -> bd.equals(boardingDate));
        if (contain) {
            throw new CustomException(SurveyErrorCode.SURVEY_INVALID_BOARDING_DATE);
        }
    }
}
