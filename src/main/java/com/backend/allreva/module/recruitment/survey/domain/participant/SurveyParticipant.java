package com.backend.allreva.module.recruitment.survey.domain.participant;

import com.backend.allreva.events.Events;
import com.backend.allreva.common.model.BaseEntity;
import com.backend.allreva.module.recruitment.survey.domain.value.BoardingType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE survey_participants SET deleted_at = NOW() WHERE id = ?")
@Table(name = "survey_participants")
public class SurveyParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long surveyId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private LocalDate boardingDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardingType boardingType;

    @Column(nullable = false)
    private int passengerNum;

    @Column(nullable = false)
    private boolean notified;

    @Builder
    private SurveyParticipant(
            final Long surveyId,
            final Long memberId,
            final LocalDate boardingDate,
            final BoardingType boardingType,
            final int passengerNum,
            final boolean notified) {
        this.surveyId = surveyId;
        this.memberId = memberId;
        this.boardingDate = boardingDate;
        this.boardingType = boardingType;
        this.passengerNum = passengerNum;
        this.notified = notified;
        Events.raise(new SurveyParticipantEvent(surveyId, passengerNum));
    }

    public void markNotified() {
        this.notified = true;
    }
}
