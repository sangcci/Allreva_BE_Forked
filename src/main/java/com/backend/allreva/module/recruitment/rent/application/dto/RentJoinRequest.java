package com.backend.allreva.module.recruitment.rent.application.dto;

import com.backend.allreva.module.recruitment.rent.domain.participant.RentParticipant;
import com.backend.allreva.module.recruitment.rent.domain.value.BoardingType;
import com.backend.allreva.module.recruitment.rent.domain.value.Depositor;
import com.backend.allreva.module.recruitment.rent.domain.value.RefundType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record RentJoinRequest(
        @NotNull Long rentId,
        @NotNull LocalDate boardingDate,
        @NotNull BoardingType boardingType,

        @NotNull @Min(value = 1, message = "탑승 인원 수는 1명 이상이어야 합니다.")
        int passengerNum,

        @NotNull String depositorName,
        @NotNull String depositorTime,
        @NotNull String phone,
        @NotNull RefundType refundType,
        @NotNull String refundAccount) {

    public RentParticipant toEntity(final Long memberId) {
        return RentParticipant.builder()
                .rentId(rentId)
                .memberId(memberId)
                .depositor(Depositor.builder()
                        .depositorName(depositorName)
                        .depositorTime(depositorTime)
                        .phone(phone)
                        .build())
                .boardingType(boardingType)
                .passengerNum(passengerNum)
                .refundType(refundType)
                .refundAccount(refundAccount)
                .boardingDate(boardingDate)
                .build();
    }
}
