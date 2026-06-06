package com.backend.allreva.recruitment.rent;

import com.backend.allreva.common.persistence.BaseEntity;
import com.backend.allreva.recruitment.rent.domain.Depositor;
import com.backend.allreva.recruitment.rent.domain.RefundType;
import com.backend.allreva.recruitment.rent.domain.RentParticipant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity(name = "RentParticipant")
@Table(name = "rent_participants")
@SQLDelete(sql = "UPDATE rent_participants SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RentParticipantEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rent_id", nullable = false)
    private RentEntity rent;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String depositorName;

    @Column(nullable = false)
    private String depositorTime;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private int passengerNum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundType refundType;

    @Column(nullable = false)
    private String refundAccount;

    @Column(nullable = false)
    private LocalDate boardingDate;

    private RentParticipantEntity(
            final Long id,
            final RentEntity rent,
            final Long memberId,
            final Depositor depositor,
            final int passengerNum,
            final RefundType refundType,
            final String refundAccount,
            final LocalDate boardingDate) {
        this.id = id;
        this.rent = rent;
        this.memberId = memberId;
        if (depositor != null) {
            this.depositorName = depositor.getDepositorName();
            this.depositorTime = depositor.getDepositorTime();
            this.phone = depositor.getPhone();
        }
        this.passengerNum = passengerNum;
        this.refundType = refundType;
        this.refundAccount = refundAccount;
        this.boardingDate = boardingDate;
    }

    public static RentParticipantEntity from(final RentParticipant participant, final RentEntity rent) {
        return new RentParticipantEntity(
                participant.getId(),
                rent,
                participant.getMemberId(),
                participant.getDepositor(),
                participant.getPassengerNum(),
                participant.getRefundType(),
                participant.getRefundAccount(),
                participant.getBoardingDate());
    }

    public RentParticipant toDomain() {
        return RentParticipant.builder()
                .id(id)
                .rentId(rent != null ? rent.getId() : null)
                .memberId(memberId)
                .depositor(Depositor.builder()
                        .depositorName(depositorName)
                        .depositorTime(depositorTime)
                        .phone(phone)
                        .build())
                .passengerNum(passengerNum)
                .refundType(refundType)
                .refundAccount(refundAccount)
                .boardingDate(boardingDate)
                .createdAt(getCreatedAt())
                .build();
    }
}
