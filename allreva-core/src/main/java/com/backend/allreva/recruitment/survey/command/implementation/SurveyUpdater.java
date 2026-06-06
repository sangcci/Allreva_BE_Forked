package com.backend.allreva.recruitment.survey.command.implementation;

import com.backend.allreva.recruitment.survey.command.input.UpdateSurveyCommand;
import com.backend.allreva.recruitment.survey.domain.Survey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SurveyUpdater {

    private final SurveyBoardingDateChecker boardingDateChecker;

    public void update(final Survey survey, final UpdateSurveyCommand command) {
        boardingDateChecker.check(survey.getConcertCode(), command.boardingDates());

        survey.update(
                command.title(),
                command.region(),
                command.endDate(),
                command.maxPassenger(),
                command.information(),
                command.boardingDates());
    }
}
