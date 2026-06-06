package com.backend.allreva.recruitment.survey.validation;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.allreva.recruitment.survey.JoinSurveyRequest;
import com.backend.allreva.recruitment.survey.OpenSurveyRequest;
import com.backend.allreva.recruitment.survey.SurveyIdRequest;
import com.backend.allreva.recruitment.survey.UpdateSurveyRequest;
import com.backend.allreva.recruitment.survey.domain.BoardingType;
import com.backend.allreva.recruitment.survey.domain.Region;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Survey 요청 검증 테스트")
class SurveyRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Nested
    @DisplayName("OpenSurveyRequest 검증")
    class Describe_OpenSurveyRequest {

        private OpenSurveyRequest request(
                String title, List<LocalDate> boardingDates, LocalDate endDate, int maxPassenger) {
            return new OpenSurveyRequest(title, "PFMOCK001", boardingDates, Region.서울, endDate, maxPassenger, "안내");
        }

        private Set<ConstraintViolation<OpenSurveyRequest>> validate(OpenSurveyRequest request) {
            return validator.validate(request);
        }

        @Test
        @DisplayName("유효한 요청이면 통과한다")
        void valid_request_passes() {
            assertThat(validate(request(
                            "제목",
                            List.of(LocalDate.now().plusDays(1)),
                            LocalDate.now().plusDays(2),
                            1)))
                    .isEmpty();
        }

        @Test
        @DisplayName("title이 빈 문자열이면 실패한다")
        void blank_title_fails() {
            assertThat(validate(request(
                            "",
                            List.of(LocalDate.now().plusDays(1)),
                            LocalDate.now().plusDays(2),
                            1)))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("title"));
        }

        @Test
        @DisplayName("boardingDates가 비어 있으면 실패한다")
        void empty_boardingDates_fails() {
            assertThat(validate(request("제목", List.of(), LocalDate.now().plusDays(2), 1)))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("boardingDates"));
        }

        @Test
        @DisplayName("endDate가 과거이면 실패한다")
        void past_endDate_fails() {
            assertThat(validate(request(
                            "제목",
                            List.of(LocalDate.now().plusDays(1)),
                            LocalDate.now().minusDays(1),
                            1)))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("endDate"));
        }

        @Test
        @DisplayName("maxPassenger가 0이면 실패한다")
        void zero_maxPassenger_fails() {
            assertThat(validate(request(
                            "제목",
                            List.of(LocalDate.now().plusDays(1)),
                            LocalDate.now().plusDays(2),
                            0)))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("maxPassenger"));
        }
    }

    @Nested
    @DisplayName("UpdateSurveyRequest 검증")
    class Describe_UpdateSurveyRequest {

        private UpdateSurveyRequest request(
                Long surveyId, String title, List<LocalDate> boardingDates, Region region, LocalDate endDate) {
            return new UpdateSurveyRequest(surveyId, title, boardingDates, region, endDate, 1, "안내");
        }

        private Set<ConstraintViolation<UpdateSurveyRequest>> validate(UpdateSurveyRequest request) {
            return validator.validate(request);
        }

        @Test
        @DisplayName("유효한 요청이면 통과한다")
        void valid_request_passes() {
            assertThat(validate(request(
                            1L,
                            "제목",
                            List.of(LocalDate.now().plusDays(1)),
                            Region.서울,
                            LocalDate.now().plusDays(2))))
                    .isEmpty();
        }

        @Test
        @DisplayName("surveyId가 null이면 실패한다")
        void null_surveyId_fails() {
            assertThat(validate(request(
                            null,
                            "제목",
                            List.of(LocalDate.now().plusDays(1)),
                            Region.서울,
                            LocalDate.now().plusDays(2))))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("surveyId"));
        }

        @Test
        @DisplayName("title이 빈 문자열이면 실패한다")
        void blank_title_fails() {
            assertThat(validate(request(
                            1L,
                            "",
                            List.of(LocalDate.now().plusDays(1)),
                            Region.서울,
                            LocalDate.now().plusDays(2))))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("title"));
        }

        @Test
        @DisplayName("boardingDates가 비어 있으면 실패한다")
        void empty_boardingDates_fails() {
            assertThat(validate(request(
                            1L, "제목", List.of(), Region.서울, LocalDate.now().plusDays(2))))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("boardingDates"));
        }

        @Test
        @DisplayName("region이 null이면 실패한다")
        void null_region_fails() {
            assertThat(validate(request(
                            1L,
                            "제목",
                            List.of(LocalDate.now().plusDays(1)),
                            null,
                            LocalDate.now().plusDays(2))))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("region"));
        }

        @Test
        @DisplayName("endDate가 과거이면 실패한다")
        void past_endDate_fails() {
            assertThat(validate(request(
                            1L,
                            "제목",
                            List.of(LocalDate.now().plusDays(1)),
                            Region.서울,
                            LocalDate.now().minusDays(1))))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("endDate"));
        }

        @Test
        @DisplayName("maxPassenger가 null이면 실패한다")
        void null_maxPassenger_fails() {
            var request = new UpdateSurveyRequest(
                    1L,
                    "제목",
                    List.of(LocalDate.now().plusDays(1)),
                    Region.서울,
                    LocalDate.now().plusDays(2),
                    null,
                    "안내");

            assertThat(validate(request))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("maxPassenger"));
        }

        @Test
        @DisplayName("maxPassenger가 0이면 실패한다")
        void zero_maxPassenger_fails() {
            var request = new UpdateSurveyRequest(
                    1L,
                    "제목",
                    List.of(LocalDate.now().plusDays(1)),
                    Region.서울,
                    LocalDate.now().plusDays(2),
                    0,
                    "안내");

            assertThat(validate(request))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("maxPassenger"));
        }
    }

    @Nested
    @DisplayName("SurveyIdRequest 검증")
    class Describe_SurveyIdRequest {

        @Test
        @DisplayName("surveyId가 있으면 통과한다")
        void valid_request_passes() {
            assertThat(validator.validate(new SurveyIdRequest(1L))).isEmpty();
        }

        @Test
        @DisplayName("surveyId가 null이면 실패한다")
        void null_surveyId_fails() {
            assertThat(validator.validate(new SurveyIdRequest(null)))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("surveyId"));
        }
    }

    @Nested
    @DisplayName("JoinSurveyRequest 검증")
    class Describe_JoinSurveyRequest {

        private JoinSurveyRequest request(
                Long surveyId, LocalDate boardingDate, BoardingType boardingType, int passengerNum) {
            return new JoinSurveyRequest(surveyId, boardingDate, boardingType, passengerNum, false);
        }

        private Set<ConstraintViolation<JoinSurveyRequest>> validate(JoinSurveyRequest request) {
            return validator.validate(request);
        }

        @Test
        @DisplayName("유효한 요청이면 통과한다")
        void valid_request_passes() {
            assertThat(validate(request(1L, LocalDate.now().plusDays(1), BoardingType.UP, 1)))
                    .isEmpty();
        }

        @Test
        @DisplayName("surveyId가 null이면 실패한다")
        void null_surveyId_fails() {
            assertThat(validate(request(null, LocalDate.now().plusDays(1), BoardingType.UP, 1)))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("surveyId"));
        }

        @Test
        @DisplayName("boardingDate가 null이면 실패한다")
        void null_boardingDate_fails() {
            assertThat(validate(request(1L, null, BoardingType.UP, 1)))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("boardingDate"));
        }

        @Test
        @DisplayName("boardingType이 null이면 실패한다")
        void null_boardingType_fails() {
            assertThat(validate(request(1L, LocalDate.now().plusDays(1), null, 1)))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("boardingType"));
        }

        @Test
        @DisplayName("passengerNum이 0이면 실패한다")
        void zero_passengerNum_fails() {
            assertThat(validate(request(1L, LocalDate.now().plusDays(1), BoardingType.UP, 0)))
                    .anyMatch(v -> v.getPropertyPath().toString().equals("passengerNum"));
        }
    }
}
