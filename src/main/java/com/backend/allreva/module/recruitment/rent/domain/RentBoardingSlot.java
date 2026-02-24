package com.backend.allreva.module.recruitment.rent.domain;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.model.BaseEntity;
import com.backend.allreva.module.recruitment.rent.exception.RentErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(
        name = "rent_boarding_slots",
        indexes = @Index(name = "idx_rent_boarding_slots_rent_date", columnList = "rent_id, date")
)
@SQLDelete(sql = "UPDATE rent_boarding_slots SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RentBoardingSlot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long rentId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int recruitmentCount;

    @Column(nullable = false)
    private int passengerCount = 0;

    @Builder
    private RentBoardingSlot(
            Long rentId,
            LocalDate date,
            int recruitmentCount) {
        this.rentId = rentId;
        this.date = date;
        this.recruitmentCount = recruitmentCount;
        this.passengerCount = 0;
    }

    public void addPassengerCount(int count) {
        if (passengerCount + count > recruitmentCount) {
            throw new CustomException(RentErrorCode.SLOT_FULL);
        }
        this.passengerCount += count;
    }
}
