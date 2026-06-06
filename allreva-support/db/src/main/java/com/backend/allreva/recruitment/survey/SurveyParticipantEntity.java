package com.backend.allreva.recruitment.survey;

import com.backend.allreva.common.persistence.BaseEntity;
import com.backend.allreva.recruitment.survey.domain.BoardingType;
import com.backend.allreva.recruitment.survey.domain.SurveyParticipant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity(name = "SurveyParticipant")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE survey_participants SET deleted_at = NOW() WHERE id = ?")
@Table(name = "survey_participants")
public class SurveyParticipantEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long surveyId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private java.time.LocalDate boardingDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardingType boardingType;

    @Column(nullable = false)
    private int passengerNum;

    @Column(nullable = false)
    private boolean notified;

    private SurveyParticipantEntity(
            final Long id,
            final Long surveyId,
            final Long memberId,
            final java.time.LocalDate boardingDate,
            final BoardingType boardingType,
            final int passengerNum,
            final boolean notified) {
        this.id = id;
        this.surveyId = surveyId;
        this.memberId = memberId;
        this.boardingDate = boardingDate;
        this.boardingType = boardingType;
        this.passengerNum = passengerNum;
        this.notified = notified;
    }

    public static SurveyParticipantEntity from(final SurveyParticipant participant) {
        return new SurveyParticipantEntity(
                participant.getId(),
                participant.getSurveyId(),
                participant.getMemberId(),
                participant.getBoardingDate(),
                participant.getBoardingType(),
                participant.getPassengerNum(),
                participant.isNotified());
    }

    public SurveyParticipant toDomain() {
        return SurveyParticipant.builder()
                .id(id)
                .surveyId(surveyId)
                .memberId(memberId)
                .boardingDate(boardingDate)
                .boardingType(boardingType)
                .passengerNum(passengerNum)
                .notified(notified)
                .build();
    }
}
