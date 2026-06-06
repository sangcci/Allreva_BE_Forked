package com.backend.allreva.recruitment.rent.query.model;

import com.backend.allreva.recruitment.rent.domain.RefundType;
import com.backend.allreva.recruitment.rent.domain.Rent;
import com.backend.allreva.recruitment.rent.domain.RentBoardingSlot;
import com.backend.allreva.recruitment.rent.domain.RentParticipant;
import com.backend.allreva.recruitment.rent.domain.Route;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record JoinedRentResult(
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

    public static JoinedRentResult from(
            final RentParticipant participant, final Rent rent, final RentBoardingSlot slot) {
        return new JoinedRentResult(
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

    public static JoinedRentResult from(final JoinedRent rent) {
        return new JoinedRentResult(
                rent.rentId(),
                rent.title(),
                rent.rentBoardingDate(),
                rent.upRoute(),
                rent.downRoute(),
                rent.rentStartDate(),
                rent.rentEndDate(),
                rent.recruitmentCount(),
                rent.participateCount(),
                rent.isClosed(),
                rent.rentParticipantId(),
                rent.applyDate(),
                rent.passengerNum(),
                rent.depositorName(),
                rent.depositorTime(),
                rent.refundType());
    }
}
