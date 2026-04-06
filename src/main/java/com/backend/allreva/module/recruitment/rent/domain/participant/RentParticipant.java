package com.backend.allreva.module.recruitment.rent.domain.participant;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.model.BaseEntity;
import com.backend.allreva.module.recruitment.rent.domain.value.Depositor;
import com.backend.allreva.module.recruitment.rent.domain.value.RefundType;
import com.backend.allreva.module.recruitment.rent.exception.RentErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "rent_participants")
@SQLDelete(sql = "UPDATE rent_participants SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RentParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long rentId;

    @Column(nullable = false)
    private Long memberId;

    @Embedded
    private Depositor depositor;

    @Column(nullable = false)
    private int passengerNum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundType refundType;

    @Column(nullable = false)
    private String refundAccount;

    @Column(nullable = false)
    private LocalDate boardingDate;

    @Builder
    private RentParticipant(
            Long rentId,
            Long memberId,
            Depositor depositor,
            int passengerNum,
            RefundType refundType,
            String refundAccount,
            LocalDate boardingDate) {
        this.rentId = rentId;
        this.memberId = memberId;
        this.depositor = depositor;
        this.passengerNum = passengerNum;
        this.refundType = refundType;
        this.refundAccount = refundAccount;
        this.boardingDate = boardingDate;
    }

    public void update(
            Depositor depositor,
            int passengerNum,
            RefundType refundType,
            String refundAccount,
            LocalDate boardingDate) {
        this.depositor = depositor;
        this.passengerNum = passengerNum;
        this.refundType = refundType;
        this.refundAccount = refundAccount;
        this.boardingDate = boardingDate;
    }

    public void validateMine(Long memberId) {
        if (!this.memberId.equals(memberId)) {
            throw new CustomException(RentErrorCode.RENT_PARTICIPANT_ACCESS_DENIED);
        }
    }
}
