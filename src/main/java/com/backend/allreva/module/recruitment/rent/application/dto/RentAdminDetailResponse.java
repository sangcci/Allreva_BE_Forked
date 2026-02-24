package com.backend.allreva.module.recruitment.rent.application.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.List;

public record RentAdminDetailResponse(
        @JsonUnwrapped
        RentAdminSummaryResponse rentAdminSummaryResponse,
        @JsonUnwrapped
        RentJoinCountResponse rentJoinCountResponse,
        List<RentJoinDetailResponse> rentJoinDetailResponses
) {

}
