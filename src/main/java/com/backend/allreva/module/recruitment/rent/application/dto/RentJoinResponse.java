package com.backend.allreva.module.recruitment.rent.application.dto;

import com.backend.allreva.module.recruitment.rent.domain.value.BoardingType;
import com.backend.allreva.module.recruitment.rent.domain.value.RefundType;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 자신이 참여한 차량 대절 마이페이지
 */
public record RentJoinResponse(
        Long rentId,
        String title,
        LocalDate rentBoardingDate,
        String boardingArea,
        LocalDateTime rentStartDate,
        LocalDate rentEndDate,
        int recruitmentCount,
        int participateCount,
        boolean isClosed,
        Long rentParticipantId,
        LocalDateTime applyDate,
        int passengerNum,
        BoardingType boardingType,
        String depositorName,
        String depositorTime,
        RefundType refundType
) {

}
