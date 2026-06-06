package com.backend.allreva.recruitment.rent.query.model;

import com.backend.allreva.recruitment.rent.domain.RefundType;
import java.time.LocalDateTime;

public record RentParticipantItem(
        Long rentParticipantId,
        LocalDateTime applyDate,
        String depositorName,
        String phone,
        int passengerNum,
        String depositorTime,
        RefundType refundType,
        String rentAccount) {}
