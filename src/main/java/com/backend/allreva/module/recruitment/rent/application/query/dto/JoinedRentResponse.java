package com.backend.allreva.module.recruitment.rent.application.query.dto;

import com.backend.allreva.module.recruitment.rent.domain.Rent;
import com.backend.allreva.module.recruitment.rent.domain.RentBoardingSlot;
import com.backend.allreva.module.recruitment.rent.domain.participant.RentParticipant;
import com.backend.allreva.module.recruitment.rent.domain.value.RefundType;
import com.backend.allreva.module.recruitment.rent.domain.value.Route;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record JoinedRentResponse(
        Long rentId,
        String title,
        LocalDate rentBoardingDate,
        Route upRoute,
        Route downRoute,
        LocalDateTime rentStartDate,
        LocalDate rentEndDate,
        int recruitmentCount,
        int participateCount,
        boolean isClosed,
        Long rentParticipantId,
        LocalDateTime applyDate,
        int passengerNum,
        String depositorName,
        String depositorTime,
        RefundType refundType) {

    public static JoinedRentResponse from(
            final RentParticipant participant, final Rent rent, final RentBoardingSlot slot) {
        return new JoinedRentResponse(
                rent.getId(),
                rent.getTitle(),
                slot.getDate(),
                rent.getUpRoute(),
                rent.getDownRoute(),
                rent.getCreatedAt(),
                rent.getEndDate(),
                slot.getRecruitmentCount(),
                slot.getPassengerCount(),
                rent.isClosed(),
                participant.getId(),
                participant.getCreatedAt(),
                participant.getPassengerNum(),
                participant.getDepositor().getDepositorName(),
                participant.getDepositor().getDepositorTime(),
                participant.getRefundType());
    }
}
