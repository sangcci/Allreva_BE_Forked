package com.backend.allreva.module.recruitment.survey.validation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.backend.allreva.module.recruitment.survey.application.command.SurveyCommandService;
import com.backend.allreva.module.recruitment.survey.application.dto.JoinSurveyRequest;
import com.backend.allreva.module.recruitment.survey.application.dto.OpenSurveyRequest;
import com.backend.allreva.module.recruitment.survey.domain.value.BoardingType;
import com.backend.allreva.module.recruitment.survey.domain.value.Region;
import com.backend.allreva.support.IntegrationTestSupport;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Survey Command 서비스 검증 테스트")
class SurveyCommandValidationIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private SurveyCommandService surveyCommandService;

    @Test
    @DisplayName("openSurvey는 controller 없이도 DTO 제약을 검증한다")
    void openSurvey_validates_request_in_service_layer() {
        OpenSurveyRequest invalidRequest = new OpenSurveyRequest(
                "",
                "PFMOCK001",
                List.of(LocalDate.now().plusDays(1)),
                Region.서울,
                LocalDate.now().plusDays(2),
                1,
                "안내");

        assertThatThrownBy(() -> surveyCommandService.openSurvey(1L, invalidRequest))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("title");
    }

    @Test
    @DisplayName("joinSurvey는 controller 없이도 DTO 제약을 검증한다")
    void joinSurvey_validates_request_in_service_layer() {
        JoinSurveyRequest invalidRequest =
                new JoinSurveyRequest(null, LocalDate.now().plusDays(1), BoardingType.UP, 1, false);

        assertThatThrownBy(() -> surveyCommandService.joinSurvey(1L, invalidRequest))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("surveyId");
    }
}
