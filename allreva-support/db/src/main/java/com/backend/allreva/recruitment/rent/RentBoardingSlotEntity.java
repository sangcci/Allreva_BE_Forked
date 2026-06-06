package com.backend.allreva.recruitment.rent;

import com.backend.allreva.common.persistence.BaseEntity;
import com.backend.allreva.recruitment.rent.domain.RentBoardingSlot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Getter
@Entity(name = "RentBoardingSlot")
@Table(name = "rent_boarding_slots")
@SQLDelete(sql = "UPDATE rent_boarding_slots SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentBoardingSlotEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rent_id", nullable = false)
    private RentEntity rent;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int recruitmentCount;

    @Column(nullable = false)
    private int passengerCount;

    private RentBoardingSlotEntity(
            final Long id, final LocalDate date, final int recruitmentCount, final int passengerCount) {
        this.id = id;
        this.date = date;
        this.recruitmentCount = recruitmentCount;
        this.passengerCount = passengerCount;
    }

    public static RentBoardingSlotEntity from(final RentBoardingSlot slot) {
        return new RentBoardingSlotEntity(
                slot.getId(), slot.getDate(), slot.getRecruitmentCount(), slot.getPassengerCount());
    }

    public RentBoardingSlot toDomain() {
        return RentBoardingSlot.builder()
                .id(id)
                .date(date)
                .recruitmentCount(recruitmentCount)
                .passengerCount(passengerCount)
                .build();
    }

    void assignRent(final RentEntity rent) {
        this.rent = rent;
    }
}
