package com.backend.allreva.module.recruitment.rent.application.command.dto;

import com.backend.allreva.module.recruitment.rent.domain.value.RefundType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record RentJoinUpdateRequest(
        @NotNull Long rentParticipantId,
        @NotNull LocalDate boardingDate,

        @Min(value = 1, message = "탑승 인원 수는 1명 이상이어야 합니다.") @Max(value = 45, message = "탑승 인원 수는 45명 이하여야 합니다.")
        int passengerNum,

        @NotBlank String depositorName,
        @NotBlank String depositorTime,

        @NotBlank @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
        String phone,

        @NotNull RefundType refundType,
        @NotBlank String refundAccount) {}
