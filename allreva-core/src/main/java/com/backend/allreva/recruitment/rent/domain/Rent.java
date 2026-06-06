package com.backend.allreva.recruitment.rent.domain;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.model.Image;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Rent {

    private Long id;
    private Long memberId;
    private String concertCode;
    private String title;
    private Image image;
    private String region;
    private BoardingType boardingType;
    private Route upRoute;
    private Route downRoute;
    private Bus bus;
    private int price;
    private LocalDate endDate;
    private String information;
    private boolean closed;
    private LocalDateTime createdAt;

    @Builder.Default
    private List<RentBoardingSlot> boardingSlots = new ArrayList<>();

    public void openBoardingSlots(final List<RentBoardingSlot> boardingSlots) {
        this.boardingSlots = boardingSlots != null ? new ArrayList<>(boardingSlots) : new ArrayList<>();
    }

    public void replaceBoardingSlots(final List<RentBoardingSlot> boardingSlots) {
        this.boardingSlots = boardingSlots != null ? new ArrayList<>(boardingSlots) : new ArrayList<>();
    }

    public void updateDetails(
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
        this.closed = false;
    }

    public void validateMine(final Long memberId) {
        if (!this.memberId.equals(memberId)) {
            throw new CustomException(RentErrorCode.RENT_ACCESS_DENIED);
        }
    }

    public void validateHostCannotJoin(final Long memberId) {
        if (this.memberId.equals(memberId)) {
            throw new CustomException(RentErrorCode.RENT_HOST_CANNOT_JOIN);
        }
    }

    public void close() {
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
}
