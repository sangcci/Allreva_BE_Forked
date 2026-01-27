package com.backend.allreva.survey.fixture;

import static com.backend.allreva.survey.fixture.SurveyRequestFixture.createOpenSurveyRequest;

import com.backend.allreva.survey.command.application.request.OpenSurveyRequest;
import com.backend.allreva.survey.command.domain.value.Region;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SurveyRequestFixture {

    public static OpenSurveyRequest createOpenSurveyRequest(Long concertId, LocalDate endDate, Region region) {
        return new OpenSurveyRequest(
                "하현상 콘서트: Elegy [서울] 수요조사 모집합니다.",
                concertId,
                List.of(LocalDate.of(2030, 12, 1), LocalDate.of(2030, 12, 2)),
                "하현상",
                region,
                endDate,
                25,
                "이틀 모두 운영합니다."
        );
    }
}
