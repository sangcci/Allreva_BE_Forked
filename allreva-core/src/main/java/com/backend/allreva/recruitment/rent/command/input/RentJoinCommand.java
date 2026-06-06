package com.backend.allreva.recruitment.rent.command.input;

import com.backend.allreva.recruitment.rent.domain.Depositor;
import com.backend.allreva.recruitment.rent.domain.RefundType;
import com.backend.allreva.recruitment.rent.domain.RentParticipant;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record RentJoinCommand(
        Long rentId,
        LocalDate boardingDate,
        int passengerNum,

        String depositorName,
        String depositorTime,

        String phone,

        RefundType refundType,
        String refundAccount) {

    public RentParticipant toParticipant(final Long memberId) {
        return RentParticipant.builder()
                .rentId(rentId)
                .memberId(memberId)
                .depositor(Depositor.builder()
                        .depositorName(depositorName)
                        .depositorTime(depositorTime)
                        .phone(phone)
                        .build())
                .passengerNum(passengerNum)
                .refundType(refundType)
                .refundAccount(refundAccount)
                .boardingDate(boardingDate)
                .build();
    }
}
