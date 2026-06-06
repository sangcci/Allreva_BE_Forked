package com.backend.allreva.recruitment.survey.validation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.backend.allreva.recruitment.survey.command.application.SurveyService;
import com.backend.allreva.recruitment.survey.command.input.JoinSurveyCommand;
import com.backend.allreva.recruitment.survey.command.input.OpenSurveyCommand;
import com.backend.allreva.recruitment.survey.domain.BoardingType;
import com.backend.allreva.recruitment.survey.domain.Region;
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
    private SurveyService surveyCommandService;

    @Test
    @DisplayName("openSurvey는 controller 없이도 DTO 제약을 검증한다")
    void openSurvey_validates_request_in_service_layer() {
        OpenSurveyCommand invalidRequest = new OpenSurveyCommand(
                "",
                "PFMOCK001",
                List.of(LocalDate.now().plusDays(1)),
                Region.서울,
                LocalDate.now().plusDays(2),
                1,
                "안내");

        assertThatThrownBy(() -> surveyCommandService.open(invalidRequest, 1L))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("title");
    }

    @Test
    @DisplayName("joinSurvey는 controller 없이도 DTO 제약을 검증한다")
    void joinSurvey_validates_request_in_service_layer() {
        JoinSurveyCommand invalidRequest =
                new JoinSurveyCommand(null, LocalDate.now().plusDays(1), BoardingType.UP, 1, false);

        assertThatThrownBy(() -> surveyCommandService.join(invalidRequest, 1L))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("surveyId");
    }
}
