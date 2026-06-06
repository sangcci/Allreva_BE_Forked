package com.backend.allreva.recruitment.survey.command.implementation;

import com.backend.allreva.recruitment.survey.command.input.OpenSurveyCommand;
import com.backend.allreva.recruitment.survey.domain.Survey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SurveyRegister {

    private final SurveyBoardingDateChecker boardingDateChecker;

    public Survey register(final OpenSurveyCommand command, final Long memberId) {
        boardingDateChecker.check(command.concertCode(), command.boardingDates());

        return Survey.builder()
                .memberId(memberId)
                .concertCode(command.concertCode())
                .title(command.title())
                .region(command.region())
                .endDate(command.endDate())
                .maxPassenger(command.maxPassenger())
                .information(command.information())
                .boardingDates(command.boardingDates())
                .closed(false)
                .build();
    }
}
