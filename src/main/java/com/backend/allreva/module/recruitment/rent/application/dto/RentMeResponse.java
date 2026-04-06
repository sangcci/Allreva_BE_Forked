package com.backend.allreva.module.recruitment.rent.application.dto;

import com.backend.allreva.module.member.domain.value.RefundAccount;
import java.time.LocalDate;
import java.util.Set;

public record RentMeResponse(Set<LocalDate> appliedDates, RefundAccount refundAccount) {}
