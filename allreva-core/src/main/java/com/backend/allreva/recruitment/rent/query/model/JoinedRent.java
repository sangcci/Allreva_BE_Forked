package com.backend.allreva.recruitment.rent.query.model;

import com.backend.allreva.recruitment.rent.domain.RefundType;
import com.backend.allreva.recruitment.rent.domain.Route;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record JoinedRent(
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
        RefundType refundType) {}
