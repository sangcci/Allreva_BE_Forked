package com.backend.allreva.recruitment.survey.command.event;

import com.backend.allreva.common.event.Event;
import com.backend.allreva.recruitment.survey.domain.Survey;
import com.backend.allreva.recruitment.survey.domain.SurveyParticipant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SurveyJoinedEvent extends Event {

    private final Long surveyId;
    private final Long hostMemberId;
    private final Long participantMemberId;
    private final Long surveyParticipantId;
    private final String title;

    public static SurveyJoinedEvent from(final Survey survey, final SurveyParticipant participant) {
        return SurveyJoinedEvent.builder()
                .surveyId(survey.getId())
                .hostMemberId(survey.getMemberId())
                .participantMemberId(participant.getMemberId())
                .surveyParticipantId(participant.getId())
                .title(survey.getTitle())
                .build();
    }
}
