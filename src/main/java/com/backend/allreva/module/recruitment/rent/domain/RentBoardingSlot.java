package com.backend.allreva.module.recruitment.rent.domain;

import com.backend.allreva.common.model.BaseEntity;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "rent_boarding_slots")
@SQLDelete(sql = "UPDATE rent_boarding_slots SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RentBoardingSlot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rent_id", nullable = false)
    private Rent rent;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int recruitmentCount;

    @Column(nullable = false)
    private int passengerCount = 0;

    @Builder
    private RentBoardingSlot(LocalDate date, int recruitmentCount) {
        this.date = date;
        this.recruitmentCount = recruitmentCount;
        this.passengerCount = 0;
    }

    void assignRent(final Rent rent) {
        this.rent = rent;
    }

    public Long getRentId() {
        return rent != null ? rent.getId() : null;
    }
}
