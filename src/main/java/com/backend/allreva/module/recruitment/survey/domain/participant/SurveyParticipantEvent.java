package com.backend.allreva.module.recruitment.survey.domain.participant;

import com.backend.allreva.common.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SurveyParticipantEvent extends Event {

    private Long surveyId;
    private int participationCount;

    public SurveyParticipantEvent(
            final Long surveyId,
            final int participationCount) {
        this.surveyId = surveyId;
        this.participationCount = participationCount;
    }
}
