package com.backend.allreva.module.recruitment.rent.application.query.dto;

import com.backend.allreva.module.recruitment.rent.domain.participant.RentParticipant;
import com.backend.allreva.module.recruitment.rent.domain.value.RefundType;
import java.time.LocalDateTime;

public record RentParticipantResponse(
        Long rentParticipantId,
        LocalDateTime applyDate,
        String depositorName,
        String phone,
        int passengerNum,
        String depositorTime,
        RefundType refundType,
        String rentAccount) {

    public static RentParticipantResponse from(final RentParticipant participant) {
        return new RentParticipantResponse(
                participant.getId(),
                participant.getCreatedAt(),
                participant.getDepositor().getDepositorName(),
                participant.getDepositor().getPhone(),
                participant.getPassengerNum(),
                participant.getDepositor().getDepositorTime(),
                participant.getRefundType(),
                participant.getRefundAccount());
    }
}
