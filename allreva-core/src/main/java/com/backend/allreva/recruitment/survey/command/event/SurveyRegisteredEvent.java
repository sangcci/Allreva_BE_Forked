package com.backend.allreva.recruitment.survey.command.event;

import com.backend.allreva.common.event.Event;
import com.backend.allreva.recruitment.survey.domain.Survey;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SurveyRegisteredEvent extends Event {

    private final Long surveyId;
    private final Long hostMemberId;
    private final String title;

    public static SurveyRegisteredEvent from(final Survey survey) {
        return SurveyRegisteredEvent.builder()
                .surveyId(survey.getId())
                .hostMemberId(survey.getMemberId())
                .title(survey.getTitle())
                .build();
    }
}
