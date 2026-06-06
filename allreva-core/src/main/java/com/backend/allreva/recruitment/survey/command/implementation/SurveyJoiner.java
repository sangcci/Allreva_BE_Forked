package com.backend.allreva.recruitment.survey.command.implementation;

import com.backend.allreva.recruitment.survey.command.input.JoinSurveyCommand;
import com.backend.allreva.recruitment.survey.domain.Survey;
import com.backend.allreva.recruitment.survey.domain.SurveyParticipant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SurveyJoiner {

    private final SurveyParticipantReader surveyParticipantReader;

    public SurveyParticipant join(final Survey survey, final JoinSurveyCommand command, final Long memberId) {
        surveyParticipantReader.validateNotAlreadyJoined(memberId, command.surveyId());
        survey.validateBoardingDate(command.boardingDate());

        return SurveyParticipant.builder()
                .surveyId(command.surveyId())
                .memberId(memberId)
                .boardingDate(command.boardingDate())
                .boardingType(command.boardingType())
                .passengerNum(command.passengerNum())
                .notified(command.notified())
                .build();
    }
}
