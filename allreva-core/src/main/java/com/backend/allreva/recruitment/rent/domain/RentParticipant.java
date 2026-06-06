package com.backend.allreva.recruitment.rent.domain;

import com.backend.allreva.common.exception.CustomException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RentParticipant {

    private Long id;
    private Long rentId;
    private Long memberId;
    private Depositor depositor;
    private int passengerNum;
    private RefundType refundType;
    private String refundAccount;
    private LocalDate boardingDate;
    private LocalDateTime createdAt;

    public void update(
            final Depositor depositor,
            final int passengerNum,
            final RefundType refundType,
            final String refundAccount,
            final LocalDate boardingDate) {
        this.depositor = depositor;
        this.passengerNum = passengerNum;
        this.refundType = refundType;
        this.refundAccount = refundAccount;
        this.boardingDate = boardingDate;
    }

    public void validateMine(final Long memberId) {
        if (!this.memberId.equals(memberId)) {
            throw new CustomException(RentErrorCode.RENT_PARTICIPANT_ACCESS_DENIED);
        }
    }
}
