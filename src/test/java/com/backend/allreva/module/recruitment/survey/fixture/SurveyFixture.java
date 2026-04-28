package com.backend.allreva.module.recruitment.survey.fixture;

import static org.instancio.Select.field;

import com.backend.allreva.module.recruitment.survey.application.dto.JoinSurveyRequest;
import com.backend.allreva.module.recruitment.survey.application.dto.OpenSurveyRequest;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveyIdRequest;
import com.backend.allreva.module.recruitment.survey.application.dto.UpdateSurveyRequest;
import com.backend.allreva.module.recruitment.survey.domain.value.BoardingType;
import com.backend.allreva.module.recruitment.survey.domain.value.Region;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.instancio.Instancio;
import org.instancio.Model;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SurveyFixture {

    public static Model<OpenSurveyRequest> openSurveyRequestModel() {
        return Instancio.of(OpenSurveyRequest.class)
                .set(field(OpenSurveyRequest.class, "endDate"), LocalDate.of(2030, 11, 30))
                .set(field(OpenSurveyRequest.class, "region"), Region.서울)
                .set(field(OpenSurveyRequest.class, "maxPassenger"), 1)
                .toModel();
    }

    public static Model<UpdateSurveyRequest> updateSurveyRequestModel() {
        return Instancio.of(UpdateSurveyRequest.class)
                .set(field(UpdateSurveyRequest.class, "endDate"), LocalDate.of(2030, 11, 30))
                .toModel();
    }

    public static Model<SurveyIdRequest> surveyIdRequestModel() {
        return Instancio.of(SurveyIdRequest.class).toModel();
    }

    public static Model<JoinSurveyRequest> joinSurveyRequestModel() {
        return Instancio.of(JoinSurveyRequest.class)
                .set(field(JoinSurveyRequest.class, "boardingType"), BoardingType.ROUND)
                .set(field(JoinSurveyRequest.class, "passengerNum"), 1)
                .toModel();
    }
}
