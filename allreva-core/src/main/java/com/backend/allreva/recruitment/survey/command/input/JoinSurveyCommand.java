package com.backend.allreva.recruitment.survey.command.input;

import com.backend.allreva.recruitment.survey.domain.BoardingType;
import java.time.LocalDate;

public record JoinSurveyCommand(
        Long surveyId, LocalDate boardingDate, BoardingType boardingType, int passengerNum, boolean notified) {}
