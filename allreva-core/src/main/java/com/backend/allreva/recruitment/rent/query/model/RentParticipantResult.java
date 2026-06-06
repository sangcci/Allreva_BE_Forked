package com.backend.allreva.recruitment.rent.query.model;

import com.backend.allreva.recruitment.rent.domain.RefundType;
import com.backend.allreva.recruitment.rent.domain.RentParticipant;
import java.time.LocalDateTime;

public record RentParticipantResult(
        Long rentParticipantId,
        LocalDateTime applyDate,
        String depositorName,
        String phone,
        int passengerNum,
        String depositorTime,
        RefundType refundType,
        String rentAccount) {

    public static RentParticipantResult from(final RentParticipant participant) {
        return new RentParticipantResult(
                participant.getId(),
                participant.getCreatedAt(),
                participant.getDepositor().getDepositorName(),
                participant.getDepositor().getPhone(),
                participant.getPassengerNum(),
                participant.getDepositor().getDepositorTime(),
                participant.getRefundType(),
                participant.getRefundAccount());
    }

    public static RentParticipantResult from(final RentParticipantItem participant) {
        return new RentParticipantResult(
                participant.rentParticipantId(),
                participant.applyDate(),
                participant.depositorName(),
                participant.phone(),
                participant.passengerNum(),
                participant.depositorTime(),
                participant.refundType(),
                participant.rentAccount());
    }
}
