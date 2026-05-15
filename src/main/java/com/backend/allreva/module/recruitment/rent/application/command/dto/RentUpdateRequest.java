package com.backend.allreva.module.recruitment.rent.application.command.dto;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.module.recruitment.rent.domain.value.BoardingType;
import com.backend.allreva.module.recruitment.rent.domain.value.BusSize;
import com.backend.allreva.module.recruitment.rent.domain.value.BusType;
import com.backend.allreva.module.recruitment.rent.domain.value.Route;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.List;

public record RentUpdateRequest(
        @NotNull Long rentId,
        @NotBlank String region,
        @NotNull BoardingType boardingType,
        @Valid Route upRoute,
        @Valid Route downRoute,

        @NotEmpty(message = "날짜는 하루 이상 선택되어야 합니다.") @JsonProperty("boardingDates")
        List<LocalDate> rentBoardingDateRequests,

        @NotNull BusSize busSize,
        @NotNull BusType busType,
        @Min(value = 1, message = "탑승 인원 수는 1명 이상이어야 합니다.") int maxPassenger,
        @PositiveOrZero int price,
        @Min(value = 1, message = "모집 인원 수는 1명 이상이어야 합니다.") int recruitmentCount,
        @FutureOrPresent(message = "마감 기한은 과거일 수 없습니다.") LocalDate endDate,
        String information,
        Image image) {

    @AssertTrue(message = "상행 경로는 필수입니다.")
    private boolean isUpRouteValid() {
        if (boardingType == null) return true;
        if (boardingType == BoardingType.UP || boardingType == BoardingType.ROUND) {
            return upRoute != null;
        }
        return true;
    }

    @AssertTrue(message = "하행 경로는 필수입니다.")
    private boolean isDownRouteValid() {
        if (boardingType == null) return true;
        if (boardingType == BoardingType.DOWN || boardingType == BoardingType.ROUND) {
            return downRoute != null;
        }
        return true;
    }
}
