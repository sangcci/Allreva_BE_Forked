package com.backend.allreva.module.recruitment.rent.validation;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinUpdateRequest;
import com.backend.allreva.module.recruitment.rent.domain.value.RefundType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Rent Join DTO 검증 테스트")
class RentJoinRequestValidationTest {

    private static Validator validator;

    static final LocalDate VALID_DATE = LocalDate.of(2030, 12, 1);

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Nested
    @DisplayName("RentJoinRequest 검증")
    class Describe_RentJoinRequest {

        private RentJoinRequest request(int passengerNum, String phone) {
            return RentJoinRequest.builder()
                    .rentId(1L)
                    .boardingDate(VALID_DATE)
                    .passengerNum(passengerNum)
                    .depositorName("홍길동")
                    .depositorTime("12:00")
                    .phone(phone)
                    .refundType(RefundType.REFUND)
                    .refundAccount("국민은행 99999")
                    .build();
        }

        private Set<ConstraintViolation<RentJoinRequest>> validate(RentJoinRequest req) {
            return validator.validate(req);
        }

        @Test
        @DisplayName("유효한 요청이면 통과한다")
        void valid_request_passes() {
            assertThat(validate(request(2, "010-1234-5678"))).isEmpty();
        }

        @Nested
        @DisplayName("passengerNum 범위 검증")
        class Describe_passengerNum {

            @Test
            @DisplayName("1명이면 통과한다")
            void min_boundary_passes() {
                assertThat(validate(request(1, "010-1234-5678"))).isEmpty();
            }

            @Test
            @DisplayName("45명이면 통과한다")
            void max_boundary_passes() {
                assertThat(validate(request(45, "010-1234-5678"))).isEmpty();
            }

            @Test
            @DisplayName("0명이면 실패한다")
            void below_min_fails() {
                var violations = validate(request(0, "010-1234-5678"));
                assertThat(violations).anyMatch(v -> v.getMessage().equals("탑승 인원 수는 1명 이상이어야 합니다."));
            }

            @Test
            @DisplayName("46명이면 실패한다")
            void above_max_fails() {
                var violations = validate(request(46, "010-1234-5678"));
                assertThat(violations).anyMatch(v -> v.getMessage().equals("탑승 인원 수는 45명 이하여야 합니다."));
            }
        }

        @Nested
        @DisplayName("phone 형식 검증")
        class Describe_phone {

            @Test
            @DisplayName("010-1234-5678 형식이면 통과한다")
            void mobile_format_passes() {
                assertThat(validate(request(2, "010-1234-5678"))).isEmpty();
            }

            @Test
            @DisplayName("02-123-4567 형식이면 통과한다")
            void landline_format_passes() {
                assertThat(validate(request(2, "02-123-4567"))).isEmpty();
            }

            @Test
            @DisplayName("하이픈 없는 번호는 실패한다")
            void no_hyphen_fails() {
                var violations = validate(request(2, "01012345678"));
                assertThat(violations).anyMatch(v -> v.getMessage().equals("전화번호 형식이 올바르지 않습니다."));
            }

            @Test
            @DisplayName("형식이 맞지 않으면 실패한다")
            void invalid_format_fails() {
                var violations = validate(request(2, "010-12-5678"));
                assertThat(violations).anyMatch(v -> v.getMessage().equals("전화번호 형식이 올바르지 않습니다."));
            }
        }
    }

    @Nested
    @DisplayName("RentJoinUpdateRequest 검증")
    class Describe_RentJoinUpdateRequest {

        private RentJoinUpdateRequest request(int passengerNum, String phone) {
            return new RentJoinUpdateRequest(
                    1L, VALID_DATE, passengerNum, "홍길동", "12:00", phone, RefundType.REFUND, "국민은행 99999");
        }

        private Set<ConstraintViolation<RentJoinUpdateRequest>> validate(RentJoinUpdateRequest req) {
            return validator.validate(req);
        }

        @Test
        @DisplayName("유효한 요청이면 통과한다")
        void valid_request_passes() {
            assertThat(validate(request(2, "010-1234-5678"))).isEmpty();
        }

        @Nested
        @DisplayName("passengerNum 범위 검증")
        class Describe_passengerNum {

            @Test
            @DisplayName("1명이면 통과한다")
            void min_boundary_passes() {
                assertThat(validate(request(1, "010-1234-5678"))).isEmpty();
            }

            @Test
            @DisplayName("45명이면 통과한다")
            void max_boundary_passes() {
                assertThat(validate(request(45, "010-1234-5678"))).isEmpty();
            }

            @Test
            @DisplayName("0명이면 실패한다")
            void below_min_fails() {
                var violations = validate(request(0, "010-1234-5678"));
                assertThat(violations).anyMatch(v -> v.getMessage().equals("탑승 인원 수는 1명 이상이어야 합니다."));
            }

            @Test
            @DisplayName("46명이면 실패한다")
            void above_max_fails() {
                var violations = validate(request(46, "010-1234-5678"));
                assertThat(violations).anyMatch(v -> v.getMessage().equals("탑승 인원 수는 45명 이하여야 합니다."));
            }
        }

        @Nested
        @DisplayName("phone 형식 검증")
        class Describe_phone {

            @Test
            @DisplayName("010-1234-5678 형식이면 통과한다")
            void mobile_format_passes() {
                assertThat(validate(request(2, "010-1234-5678"))).isEmpty();
            }

            @Test
            @DisplayName("하이픈 없는 번호는 실패한다")
            void no_hyphen_fails() {
                var violations = validate(request(2, "01012345678"));
                assertThat(violations).anyMatch(v -> v.getMessage().equals("전화번호 형식이 올바르지 않습니다."));
            }

            @Test
            @DisplayName("형식이 맞지 않으면 실패한다")
            void invalid_format_fails() {
                var violations = validate(request(2, "010-12-5678"));
                assertThat(violations).anyMatch(v -> v.getMessage().equals("전화번호 형식이 올바르지 않습니다."));
            }
        }
    }
}
