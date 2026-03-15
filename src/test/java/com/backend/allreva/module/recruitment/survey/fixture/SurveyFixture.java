package com.backend.allreva.module.recruitment.survey.fixture;

import com.backend.allreva.module.recruitment.survey.application.dto.JoinSurveyRequest;
import com.backend.allreva.module.recruitment.survey.application.dto.OpenSurveyRequest;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveyIdRequest;
import com.backend.allreva.module.recruitment.survey.application.dto.UpdateSurveyRequest;
import com.backend.allreva.module.recruitment.survey.domain.value.BoardingType;
import com.backend.allreva.module.recruitment.survey.domain.value.Region;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SurveyFixture {

    public static OpenSurveyRequest createOpenSurveyRequest(Long concertId, List<LocalDate> dates) {
        return new OpenSurveyRequest(
                "테스트 수요조사", concertId, dates, "테스트 아티스트", Region.서울, LocalDate.of(2030, 11, 30), 45, "테스트 수요조사 정보");
    }

    public static UpdateSurveyRequest createUpdateSurveyRequest(Long surveyId, List<LocalDate> dates) {
        return new UpdateSurveyRequest(
                surveyId, "수정된 수요조사", dates, Region.서울, LocalDate.of(2030, 11, 30), 30, "수정된 정보");
    }

    public static SurveyIdRequest createSurveyIdRequest(Long surveyId) {
        return new SurveyIdRequest(surveyId);
    }

    public static JoinSurveyRequest createJoinSurveyRequest(Long surveyId, LocalDate boardingDate) {
        return new JoinSurveyRequest(surveyId, boardingDate, BoardingType.ROUND, 2, false);
    }
}
