package com.backend.allreva.recruitment.survey;

import com.backend.allreva.common.persistence.BaseEntity;
import com.backend.allreva.recruitment.survey.domain.Region;
import com.backend.allreva.recruitment.survey.domain.Survey;
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
@Entity(name = "Survey")
@Table(name = "survey")
public class SurveyEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "boarding_dates", columnDefinition = "jsonb", nullable = false)
    private List<LocalDate> boardingDates = new ArrayList<>();

    @Column(nullable = false)
    private Region region;

    @Column(nullable = false, name = "eddate")
    private LocalDate endDate;

    @Column(nullable = false)
    private int maxPassenger;

    @Column(nullable = true)
    private String information;

    @Column(nullable = false)
    private String concertCode;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private boolean isClosed;

    private SurveyEntity(
            final Long id,
            final Long memberId,
            final String concertCode,
            final String title,
            final Region region,
            final LocalDate endDate,
            final int maxPassenger,
            final String information,
            final List<LocalDate> boardingDates,
            final boolean isClosed) {
        this.id = id;
        this.memberId = memberId;
        this.concertCode = concertCode;
        this.title = title;
        this.region = region;
        this.endDate = endDate;
        this.maxPassenger = maxPassenger;
        this.information = information;
        this.boardingDates = boardingDates != null ? new ArrayList<>(boardingDates) : new ArrayList<>();
        this.isClosed = isClosed;
    }

    public static SurveyEntity from(final Survey survey) {
        return new SurveyEntity(
                survey.getId(),
                survey.getMemberId(),
                survey.getConcertCode(),
                survey.getTitle(),
                survey.getRegion(),
                survey.getEndDate(),
                survey.getMaxPassenger(),
                survey.getInformation(),
                survey.getBoardingDates(),
                survey.isClosed());
    }

    public Survey toDomain() {
        return Survey.builder()
                .id(id)
                .memberId(memberId)
                .concertCode(concertCode)
                .title(title)
                .region(region)
                .endDate(endDate)
                .maxPassenger(maxPassenger)
                .information(information)
                .boardingDates(boardingDates)
                .closed(isClosed)
                .build();
    }
}
