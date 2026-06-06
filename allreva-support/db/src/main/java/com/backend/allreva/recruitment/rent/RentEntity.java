package com.backend.allreva.recruitment.rent;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.common.persistence.BaseEntity;
import com.backend.allreva.recruitment.rent.domain.BoardingType;
import com.backend.allreva.recruitment.rent.domain.Bus;
import com.backend.allreva.recruitment.rent.domain.BusSize;
import com.backend.allreva.recruitment.rent.domain.BusType;
import com.backend.allreva.recruitment.rent.domain.Rent;
import com.backend.allreva.recruitment.rent.domain.Route;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is NULL")
@SQLDelete(sql = "UPDATE rent SET deleted_at = NOW() WHERE id = ?")
@Entity(name = "Rent")
@Table(name = "rent")
public class RentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String concertCode;

    @Column(nullable = false)
    private String title;

    @Column(name = "image", nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String region;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardingType boardingType;

    @Column(name = "up_boarding_area")
    private String upBoardingArea;

    @Column(name = "up_drop_off_area")
    private String upDropOffArea;

    @Column(name = "up_time")
    private String upTime;

    @Column(name = "down_boarding_area")
    private String downBoardingArea;

    @Column(name = "down_drop_off_area")
    private String downDropOffArea;

    @Column(name = "down_time")
    private String downTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusSize busSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusType busType;

    @Column(nullable = false)
    private int maxPassenger;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private LocalDate endDate;

    private String information;

    @Column(nullable = false)
    private boolean isClosed;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "rent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RentBoardingSlotEntity> boardingSlots = new ArrayList<>();

    private RentEntity(
            final Long id,
            final Long memberId,
            final String concertCode,
            final String title,
            final Image image,
            final String region,
            final BoardingType boardingType,
            final Route upRoute,
            final Route downRoute,
            final Bus bus,
            final int price,
            final LocalDate endDate,
            final String information,
            final boolean isClosed) {
        this.id = id;
        this.memberId = memberId;
        this.concertCode = concertCode;
        this.title = title;
        this.imageUrl = image != null ? image.getUrl() : null;
        this.region = region;
        this.boardingType = boardingType;
        if (upRoute != null) {
            this.upBoardingArea = upRoute.getBoardingArea();
            this.upDropOffArea = upRoute.getDropOffArea();
            this.upTime = upRoute.getTime();
        }
        if (downRoute != null) {
            this.downBoardingArea = downRoute.getBoardingArea();
            this.downDropOffArea = downRoute.getDropOffArea();
            this.downTime = downRoute.getTime();
        }
        if (bus != null) {
            this.busSize = bus.getBusSize();
            this.busType = bus.getBusType();
            this.maxPassenger = bus.getMaxPassenger();
        }
        this.price = price;
        this.endDate = endDate;
        this.information = information;
        this.isClosed = isClosed;
    }

    public static RentEntity from(final Rent rent) {
        RentEntity entity = new RentEntity(
                rent.getId(),
                rent.getMemberId(),
                rent.getConcertCode(),
                rent.getTitle(),
                rent.getImage(),
                rent.getRegion(),
                rent.getBoardingType(),
                rent.getUpRoute(),
                rent.getDownRoute(),
                rent.getBus(),
                rent.getPrice(),
                rent.getEndDate(),
                rent.getInformation(),
                rent.isClosed());
        entity.replaceBoardingSlots(rent.getBoardingSlots().stream()
                .map(RentBoardingSlotEntity::from)
                .toList());
        return entity;
    }

    public Rent toDomain() {
        return Rent.builder()
                .id(id)
                .memberId(memberId)
                .concertCode(concertCode)
                .title(title)
                .image(imageUrl != null ? new Image(imageUrl) : null)
                .region(region)
                .boardingType(boardingType)
                .upRoute(Route.builder()
                        .boardingArea(upBoardingArea)
                        .dropOffArea(upDropOffArea)
                        .time(upTime)
                        .build())
                .downRoute(Route.builder()
                        .boardingArea(downBoardingArea)
                        .dropOffArea(downDropOffArea)
                        .time(downTime)
                        .build())
                .bus(Bus.builder()
                        .busSize(busSize)
                        .busType(busType)
                        .maxPassenger(maxPassenger)
                        .build())
                .price(price)
                .endDate(endDate)
                .information(information)
                .closed(isClosed)
                .createdAt(getCreatedAt())
                .boardingSlots(boardingSlots.stream()
                        .map(RentBoardingSlotEntity::toDomain)
                        .toList())
                .build();
    }

    private void addBoardingSlot(final RentBoardingSlotEntity slot) {
        boardingSlots.add(slot);
        slot.assignRent(this);
    }

    private void replaceBoardingSlots(final List<RentBoardingSlotEntity> slots) {
        boardingSlots.clear();
        slots.forEach(this::addBoardingSlot);
    }
}
