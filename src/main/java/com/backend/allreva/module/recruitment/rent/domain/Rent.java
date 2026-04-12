package com.backend.allreva.module.recruitment.rent.domain;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.model.BaseEntity;
import com.backend.allreva.common.model.Image;
import com.backend.allreva.module.recruitment.rent.domain.value.BoardingType;
import com.backend.allreva.module.recruitment.rent.domain.value.Bus;
import com.backend.allreva.module.recruitment.rent.domain.value.Route;
import com.backend.allreva.module.recruitment.rent.exception.RentErrorCode;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is NULL")
@SQLDelete(sql = "UPDATE rent SET deleted_at = NOW() WHERE id = ?")
@Entity
@Table(name = "rent")
public class Rent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long concertId;

    @Column(nullable = false)
    private String title;

    @Embedded
    @AttributeOverride(name = "url", column = @Column(name = "image", nullable = false))
    private Image image;

    @Column(nullable = false)
    private String artistName;

    @Column(nullable = false)
    private String region;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardingType boardingType;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "boardingArea", column = @Column(name = "up_boarding_area")),
        @AttributeOverride(name = "dropOffArea", column = @Column(name = "up_drop_off_area")),
        @AttributeOverride(name = "time", column = @Column(name = "up_time"))
    })
    private Route upRoute;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "boardingArea", column = @Column(name = "down_boarding_area")),
        @AttributeOverride(name = "dropOffArea", column = @Column(name = "down_drop_off_area")),
        @AttributeOverride(name = "time", column = @Column(name = "down_time"))
    })
    private Route downRoute;

    @Embedded
    private Bus bus;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private LocalDate endDate;

    private String information;

    @Builder.Default
    @Column(nullable = false)
    private boolean isClosed = false;

    @Builder.Default
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "rent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RentBoardingSlot> boardingSlots = new ArrayList<>();

    public void addBoardingSlot(final RentBoardingSlot slot) {
        boardingSlots.add(slot);
        slot.assignRent(this);
    }

    public void replaceBoardingSlots(final List<RentBoardingSlot> newSlots) {
        boardingSlots.clear();
        newSlots.forEach(this::addBoardingSlot);
    }

    public void updateRent(
            final Image image,
            final String region,
            final BoardingType boardingType,
            final Route upRoute,
            final Route downRoute,
            final Bus bus,
            final int price,
            final LocalDate endDate,
            final String information) {
        this.image = image;
        this.region = region;
        this.boardingType = boardingType;
        this.upRoute = upRoute;
        this.downRoute = downRoute;
        this.bus = bus;
        this.price = price;
        this.endDate = endDate;
        this.information = information;
        this.isClosed = false;
    }

    public void validateMine(Long memberId) {
        if (!this.memberId.equals(memberId)) {
            throw new CustomException(RentErrorCode.RENT_ACCESS_DENIED);
        }
    }

    public void close() {
        isClosed = true;
    }
}
