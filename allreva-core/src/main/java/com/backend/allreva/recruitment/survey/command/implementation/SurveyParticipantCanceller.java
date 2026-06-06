package com.backend.allreva.recruitment.survey.command.implementation;

import com.backend.allreva.recruitment.survey.domain.SurveyParticipant;
import org.springframework.stereotype.Component;

@Component
public class SurveyParticipantCanceller {

    public void cancel(final SurveyParticipant participant, final Long memberId) {
        participant.validateOwner(memberId);
    }
}
