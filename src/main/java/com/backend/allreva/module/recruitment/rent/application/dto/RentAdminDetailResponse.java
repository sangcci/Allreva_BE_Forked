package com.backend.allreva.module.recruitment.rent.application.dto;

import com.backend.allreva.rent_join.query.response.RentJoinCountResponse;
import com.backend.allreva.rent_join.query.response.RentJoinDetailResponse;
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
