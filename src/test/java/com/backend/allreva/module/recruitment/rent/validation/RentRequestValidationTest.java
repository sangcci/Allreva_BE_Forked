package com.backend.allreva.module.recruitment.rent.validation;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.module.recruitment.rent.application.command.dto.RentRegisterRequest;
import com.backend.allreva.module.recruitment.rent.application.command.dto.RentUpdateRequest;
import com.backend.allreva.module.recruitment.rent.domain.value.BoardingType;
import com.backend.allreva.module.recruitment.rent.domain.value.BusSize;
import com.backend.allreva.module.recruitment.rent.domain.value.BusType;
import com.backend.allreva.module.recruitment.rent.domain.value.Route;
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

@DisplayName("Rent DTO 검증 테스트")
class RentRequestValidationTest {

    private static Validator validator;

    static final Route VALID_UP_ROUTE =
            Route.builder().boardingArea("서울역").dropOffArea("공연장").time("10:00").build();
    static final Route VALID_DOWN_ROUTE =
            Route.builder().boardingArea("공연장").dropOffArea("서울역").time("22:00").build();
    static final List<LocalDate> VALID_DATES = List.of(LocalDate.of(2030, 12, 1));
    static final LocalDate VALID_END_DATE = LocalDate.of(2030, 11, 30);

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Nested
    @DisplayName("RentRegisterRequest 검증")
    class Describe_RentRegisterRequest {

        private RentRegisterRequest request(BoardingType boardingType, Route upRoute, Route downRoute) {
            return new RentRegisterRequest(
                    "PFMOCK001",
                    "제목",
                    "서울",
                    boardingType,
                    upRoute,
                    downRoute,
                    VALID_DATES,
                    BusSize.LARGE,
                    BusType.STANDARD,
                    45,
                    50000,
                    30,
                    VALID_END_DATE,
                    null,
                    new Image("img.jpg"));
        }

        private Set<ConstraintViolation<RentRegisterRequest>> validate(RentRegisterRequest req) {
            return validator.validate(req);
        }

        @Nested
        @DisplayName("Route 조건부 필수 검증")
        class Describe_route_required {

            @Test
            @DisplayName("ROUND - upRoute, downRoute 모두 있으면 통과한다")
            void round_both_routes_passes() {
                assertThat(validate(request(BoardingType.ROUND, VALID_UP_ROUTE, VALID_DOWN_ROUTE)))
                        .isEmpty();
            }

            @Test
            @DisplayName("ROUND - upRoute 없으면 실패한다")
            void round_missing_upRoute_fails() {
                var violations = validate(request(BoardingType.ROUND, null, VALID_DOWN_ROUTE));
                assertThat(violations).anyMatch(v -> v.getMessage().equals("상행 경로는 필수입니다."));
            }

            @Test
            @DisplayName("ROUND - downRoute 없으면 실패한다")
            void round_missing_downRoute_fails() {
                var violations = validate(request(BoardingType.ROUND, VALID_UP_ROUTE, null));
                assertThat(violations).anyMatch(v -> v.getMessage().equals("하행 경로는 필수입니다."));
            }

            @Test
            @DisplayName("UP - upRoute만 있으면 통과한다")
            void up_only_upRoute_passes() {
                assertThat(validate(request(BoardingType.UP, VALID_UP_ROUTE, null)))
                        .isEmpty();
            }

            @Test
            @DisplayName("UP - upRoute 없으면 실패한다")
            void up_missing_upRoute_fails() {
                var violations = validate(request(BoardingType.UP, null, null));
                assertThat(violations).anyMatch(v -> v.getMessage().equals("상행 경로는 필수입니다."));
            }

            @Test
            @DisplayName("DOWN - downRoute만 있으면 통과한다")
            void down_only_downRoute_passes() {
                assertThat(validate(request(BoardingType.DOWN, null, VALID_DOWN_ROUTE)))
                        .isEmpty();
            }

            @Test
            @DisplayName("DOWN - downRoute 없으면 실패한다")
            void down_missing_downRoute_fails() {
                var violations = validate(request(BoardingType.DOWN, null, null));
                assertThat(violations).anyMatch(v -> v.getMessage().equals("하행 경로는 필수입니다."));
            }
        }

        @Nested
        @DisplayName("Route 내부 필드 검증")
        class Describe_route_fields {

            @Test
            @DisplayName("boardingArea가 빈 문자열이면 실패한다")
            void blank_boardingArea_fails() {
                var invalidRoute = Route.builder()
                        .boardingArea("")
                        .dropOffArea("공연장")
                        .time("10:00")
                        .build();
                var violations = validate(request(BoardingType.UP, invalidRoute, null));
                assertThat(violations).isNotEmpty();
            }

            @Test
            @DisplayName("dropOffArea가 빈 문자열이면 실패한다")
            void blank_dropOffArea_fails() {
                var invalidRoute = Route.builder()
                        .boardingArea("서울역")
                        .dropOffArea("")
                        .time("10:00")
                        .build();
                var violations = validate(request(BoardingType.UP, invalidRoute, null));
                assertThat(violations).isNotEmpty();
            }

            @Test
            @DisplayName("time이 빈 문자열이면 실패한다")
            void blank_time_fails() {
                var invalidRoute = Route.builder()
                        .boardingArea("서울역")
                        .dropOffArea("공연장")
                        .time("")
                        .build();
                var violations = validate(request(BoardingType.UP, invalidRoute, null));
                assertThat(violations).isNotEmpty();
            }
        }

        @Nested
        @DisplayName("boardingType null 시 null guard")
        class Describe_null_guard {

            @Test
            @DisplayName("boardingType이 null이면 @NotNull만 실패하고 Route 조건부 검증은 실행되지 않는다")
            void null_boardingType_skips_route_check() {
                var violations = validate(request(null, null, null));
                assertThat(violations)
                        .anyMatch(v -> v.getPropertyPath().toString().equals("boardingType"));
                assertThat(violations).noneMatch(v -> v.getMessage().contains("경로는 필수"));
            }
        }

        @Nested
        @DisplayName("필드 기본 검증")
        class Describe_field_constraints {

            @Test
            @DisplayName("title이 빈 문자열이면 실패한다")
            void blank_title_fails() {
                var req = new RentRegisterRequest(
                        "PFMOCK001",
                        "",
                        "서울",
                        BoardingType.ROUND,
                        VALID_UP_ROUTE,
                        VALID_DOWN_ROUTE,
                        VALID_DATES,
                        BusSize.LARGE,
                        BusType.STANDARD,
                        45,
                        50000,
                        30,
                        VALID_END_DATE,
                        null,
                        new Image("img.jpg"));
                assertThat(validate(req))
                        .anyMatch(v -> v.getPropertyPath().toString().equals("title"));
            }

            @Test
            @DisplayName("endDate가 과거이면 실패한다")
            void past_endDate_fails() {
                var req = new RentRegisterRequest(
                        "PFMOCK001",
                        "제목",
                        "서울",
                        BoardingType.ROUND,
                        VALID_UP_ROUTE,
                        VALID_DOWN_ROUTE,
                        VALID_DATES,
                        BusSize.LARGE,
                        BusType.STANDARD,
                        45,
                        50000,
                        30,
                        LocalDate.of(2020, 1, 1),
                        null,
                        new Image("img.jpg"));
                assertThat(validate(req))
                        .anyMatch(v -> v.getPropertyPath().toString().equals("endDate"));
            }

            @Test
            @DisplayName("maxPassenger가 0이면 실패한다")
            void zero_maxPassenger_fails() {
                var req = new RentRegisterRequest(
                        "PFMOCK001",
                        "제목",
                        "서울",
                        BoardingType.ROUND,
                        VALID_UP_ROUTE,
                        VALID_DOWN_ROUTE,
                        VALID_DATES,
                        BusSize.LARGE,
                        BusType.STANDARD,
                        0,
                        50000,
                        30,
                        VALID_END_DATE,
                        null,
                        new Image("img.jpg"));
                assertThat(validate(req))
                        .anyMatch(v -> v.getPropertyPath().toString().equals("maxPassenger"));
            }

            @Test
            @DisplayName("price가 음수이면 실패한다")
            void negative_price_fails() {
                var req = new RentRegisterRequest(
                        "PFMOCK001",
                        "제목",
                        "서울",
                        BoardingType.ROUND,
                        VALID_UP_ROUTE,
                        VALID_DOWN_ROUTE,
                        VALID_DATES,
                        BusSize.LARGE,
                        BusType.STANDARD,
                        45,
                        -1,
                        30,
                        VALID_END_DATE,
                        null,
                        new Image("img.jpg"));
                assertThat(validate(req))
                        .anyMatch(v -> v.getPropertyPath().toString().equals("price"));
            }

            @Test
            @DisplayName("boardingDates가 비어 있으면 실패한다")
            void empty_boardingDates_fails() {
                var req = new RentRegisterRequest(
                        "PFMOCK001",
                        "제목",
                        "서울",
                        BoardingType.ROUND,
                        VALID_UP_ROUTE,
                        VALID_DOWN_ROUTE,
                        List.of(),
                        BusSize.LARGE,
                        BusType.STANDARD,
                        45,
                        50000,
                        30,
                        VALID_END_DATE,
                        null,
                        new Image("img.jpg"));
                assertThat(validate(req)).anyMatch(v -> v.getMessage().equals("날짜는 하루 이상 선택되어야 합니다."));
            }
        }
    }

    @Nested
    @DisplayName("RentUpdateRequest 검증")
    class Describe_RentUpdateRequest {

        private RentUpdateRequest request(BoardingType boardingType, Route upRoute, Route downRoute) {
            return new RentUpdateRequest(
                    1L,
                    "서울",
                    boardingType,
                    upRoute,
                    downRoute,
                    VALID_DATES,
                    BusSize.LARGE,
                    BusType.STANDARD,
                    45,
                    50000,
                    30,
                    VALID_END_DATE,
                    null,
                    new Image("img.jpg"));
        }

        private Set<ConstraintViolation<RentUpdateRequest>> validate(RentUpdateRequest req) {
            return validator.validate(req);
        }

        @Nested
        @DisplayName("Route 조건부 필수 검증")
        class Describe_route_required {

            @Test
            @DisplayName("ROUND - upRoute, downRoute 모두 있으면 통과한다")
            void round_both_routes_passes() {
                assertThat(validate(request(BoardingType.ROUND, VALID_UP_ROUTE, VALID_DOWN_ROUTE)))
                        .isEmpty();
            }

            @Test
            @DisplayName("ROUND - upRoute 없으면 실패한다")
            void round_missing_upRoute_fails() {
                var violations = validate(request(BoardingType.ROUND, null, VALID_DOWN_ROUTE));
                assertThat(violations).anyMatch(v -> v.getMessage().equals("상행 경로는 필수입니다."));
            }

            @Test
            @DisplayName("ROUND - downRoute 없으면 실패한다")
            void round_missing_downRoute_fails() {
                var violations = validate(request(BoardingType.ROUND, VALID_UP_ROUTE, null));
                assertThat(violations).anyMatch(v -> v.getMessage().equals("하행 경로는 필수입니다."));
            }

            @Test
            @DisplayName("UP - upRoute만 있으면 통과한다")
            void up_only_upRoute_passes() {
                assertThat(validate(request(BoardingType.UP, VALID_UP_ROUTE, null)))
                        .isEmpty();
            }

            @Test
            @DisplayName("UP - upRoute 없으면 실패한다")
            void up_missing_upRoute_fails() {
                var violations = validate(request(BoardingType.UP, null, null));
                assertThat(violations).anyMatch(v -> v.getMessage().equals("상행 경로는 필수입니다."));
            }

            @Test
            @DisplayName("DOWN - downRoute만 있으면 통과한다")
            void down_only_downRoute_passes() {
                assertThat(validate(request(BoardingType.DOWN, null, VALID_DOWN_ROUTE)))
                        .isEmpty();
            }

            @Test
            @DisplayName("DOWN - downRoute 없으면 실패한다")
            void down_missing_downRoute_fails() {
                var violations = validate(request(BoardingType.DOWN, null, null));
                assertThat(violations).anyMatch(v -> v.getMessage().equals("하행 경로는 필수입니다."));
            }
        }

        @Nested
        @DisplayName("boardingType null 시 null guard")
        class Describe_null_guard {

            @Test
            @DisplayName("boardingType이 null이면 @NotNull만 실패하고 Route 조건부 검증은 실행되지 않는다")
            void null_boardingType_skips_route_check() {
                var violations = validate(request(null, null, null));
                assertThat(violations)
                        .anyMatch(v -> v.getPropertyPath().toString().equals("boardingType"));
                assertThat(violations).noneMatch(v -> v.getMessage().contains("경로는 필수"));
            }
        }
    }
}
