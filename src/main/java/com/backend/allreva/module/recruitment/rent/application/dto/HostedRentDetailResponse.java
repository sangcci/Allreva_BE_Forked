package com.backend.allreva.module.recruitment.rent.application.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.List;

public record HostedRentDetailResponse(
        @JsonUnwrapped HostedRentSummaryResponse rentSummary, List<JoinedRentDetailResponse> participants) {}
