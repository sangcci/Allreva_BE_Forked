package com.backend.allreva.recruitment.survey.fixture;

import static org.instancio.Select.field;

import com.backend.allreva.recruitment.survey.command.input.JoinSurveyCommand;
import com.backend.allreva.recruitment.survey.command.input.OpenSurveyCommand;
import com.backend.allreva.recruitment.survey.command.input.SurveyIdCommand;
import com.backend.allreva.recruitment.survey.command.input.UpdateSurveyCommand;
import com.backend.allreva.recruitment.survey.domain.BoardingType;
import com.backend.allreva.recruitment.survey.domain.Region;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.instancio.Instancio;
import org.instancio.Model;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SurveyFixture {

    public static Model<OpenSurveyCommand> openSurveyRequestModel() {
        return Instancio.of(OpenSurveyCommand.class)
                .set(field(OpenSurveyCommand.class, "endDate"), LocalDate.of(2030, 11, 30))
                .set(field(OpenSurveyCommand.class, "region"), Region.서울)
                .set(field(OpenSurveyCommand.class, "maxPassenger"), 1)
                .toModel();
    }

    public static Model<UpdateSurveyCommand> updateSurveyRequestModel() {
        return Instancio.of(UpdateSurveyCommand.class)
                .set(field(UpdateSurveyCommand.class, "endDate"), LocalDate.of(2030, 11, 30))
                .toModel();
    }

    public static Model<SurveyIdCommand> surveyIdRequestModel() {
        return Instancio.of(SurveyIdCommand.class).toModel();
    }

    public static Model<JoinSurveyCommand> joinSurveyRequestModel() {
        return Instancio.of(JoinSurveyCommand.class)
                .set(field(JoinSurveyCommand.class, "boardingType"), BoardingType.ROUND)
                .set(field(JoinSurveyCommand.class, "passengerNum"), 1)
                .toModel();
    }
}
