package com.backend.allreva.recruitment.rent.command.input;

import com.backend.allreva.recruitment.rent.domain.RefundType;
import java.time.LocalDate;

public record RentJoinUpdateCommand(
        Long rentParticipantId,
        LocalDate boardingDate,
        int passengerNum,

        String depositorName,
        String depositorTime,

        String phone,

        RefundType refundType,
        String refundAccount) {}
