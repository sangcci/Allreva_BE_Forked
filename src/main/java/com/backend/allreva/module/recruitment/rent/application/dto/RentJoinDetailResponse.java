package com.backend.allreva.module.recruitment.rent.application.dto;

import com.backend.allreva.module.recruitment.rent.domain.participant.RentParticipant;
import com.backend.allreva.module.recruitment.rent.domain.value.BoardingType;
import com.backend.allreva.module.recruitment.rent.domain.value.RefundType;
import java.time.LocalDateTime;

public record RentJoinDetailResponse(
        Long rentParticipantId,
        LocalDateTime applyDate,
        String depositorName,
        String phone,
        int passengerNum,
        BoardingType boardingType,
        String depositorTime,
        RefundType refundType,
        String rentAccount) {

    public static RentJoinDetailResponse from(final RentParticipant participant) {
        return new RentJoinDetailResponse(
                participant.getId(),
                participant.getCreatedAt(),
                participant.getDepositor().getDepositorName(),
                participant.getDepositor().getPhone(),
                participant.getPassengerNum(),
                participant.getBoardingType(),
                participant.getDepositor().getDepositorTime(),
                participant.getRefundType(),
                participant.getRefundAccount());
    }
}
