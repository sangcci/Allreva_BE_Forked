package com.backend.allreva.recruitment.survey.command.implementation;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.recruitment.survey.domain.SurveyErrorCode;
import com.backend.allreva.recruitment.survey.domain.SurveyParticipant;
import com.backend.allreva.recruitment.survey.domain.SurveyParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SurveyParticipantReader {

    private final SurveyParticipantRepository surveyParticipantRepository;

    public SurveyParticipant get(final Long participantId) {
        return surveyParticipantRepository
                .findById(participantId)
                .orElseThrow(() -> new CustomException(SurveyErrorCode.SURVEY_PARTICIPANT_NOT_FOUND));
    }

    public void validateNotAlreadyJoined(final Long memberId, final Long surveyId) {
        if (surveyParticipantRepository.existsByMemberIdAndSurveyId(memberId, surveyId)) {
            throw new CustomException(SurveyErrorCode.SURVEY_JOIN_ALREADY_EXISTS);
        }
    }
}
