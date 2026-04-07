package com.backend.allreva.module.recruitment.rent.fixture;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.module.recruitment.rent.application.dto.RentIdRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentRegisterRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentUpdateRequest;
import com.backend.allreva.module.recruitment.rent.domain.value.BoardingType;
import com.backend.allreva.module.recruitment.rent.domain.value.BusSize;
import com.backend.allreva.module.recruitment.rent.domain.value.BusType;
import com.backend.allreva.module.recruitment.rent.domain.value.RefundType;
import com.backend.allreva.module.recruitment.rent.domain.value.Route;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RentFixture {

    private static final Route UP_ROUTE = Route.builder()
            .boardingArea("서울역 앞")
            .dropOffArea("공연장 B건물")
            .time("10:00")
            .build();
    private static final Route DOWN_ROUTE = Route.builder()
            .boardingArea("공연장 정문")
            .dropOffArea("서울역 앞")
            .time("22:00")
            .build();

    public static RentRegisterRequest createRentRegisterRequest(Long concertId, List<LocalDate> dates) {
        return createRentRegisterRequest(concertId, dates, 30);
    }

    public static RentRegisterRequest createRentRegisterRequest(
            Long concertId, List<LocalDate> dates, int recruitmentCount) {
        return new RentRegisterRequest(
                concertId,
                "테스트 차대절",
                "테스트 아티스트",
                "서울",
                BoardingType.ROUND,
                UP_ROUTE,
                DOWN_ROUTE,
                dates,
                BusSize.LARGE,
                BusType.STANDARD,
                45,
                50000,
                recruitmentCount,
                LocalDate.of(2030, 11, 30),
                "테스트 차대절 정보",
                new Image("https://example.com/rent.jpg"));
    }

    public static RentUpdateRequest createRentUpdateRequest(Long rentId, List<LocalDate> dates) {
        return new RentUpdateRequest(
                rentId,
                "서울",
                BoardingType.ROUND,
                Route.builder()
                        .boardingArea("부산역 앞")
                        .dropOffArea("공연장 C건물")
                        .time("09:00")
                        .build(),
                Route.builder()
                        .boardingArea("공연장 정문")
                        .dropOffArea("부산역 앞")
                        .time("23:00")
                        .build(),
                dates,
                BusSize.MEDIUM,
                BusType.DELUXE,
                40,
                60000,
                20,
                LocalDate.of(2030, 11, 30),
                "수정된 차대절 정보",
                new Image("https://example.com/rent-updated.jpg"));
    }

    public static RentIdRequest createRentIdRequest(Long rentId) {
        return new RentIdRequest(rentId);
    }

    public static RentJoinRequest createRentJoinRequest(Long rentId, LocalDate date, int passengerNum) {
        return RentJoinRequest.builder()
                .rentId(rentId)
                .boardingDate(date)
                .passengerNum(passengerNum)
                .depositorName("홍길동")
                .depositorTime("12:00")
                .phone("010-1234-5678")
                .refundType(RefundType.REFUND)
                .refundAccount("국민은행 99999")
                .build();
    }
}
