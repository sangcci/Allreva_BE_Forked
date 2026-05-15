package com.backend.allreva.module.recruitment.rent.fixture;

import static org.instancio.Select.field;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.module.recruitment.rent.application.command.dto.RentIdRequest;
import com.backend.allreva.module.recruitment.rent.application.command.dto.RentJoinIdRequest;
import com.backend.allreva.module.recruitment.rent.application.command.dto.RentJoinRequest;
import com.backend.allreva.module.recruitment.rent.application.command.dto.RentJoinUpdateRequest;
import com.backend.allreva.module.recruitment.rent.application.command.dto.RentRegisterRequest;
import com.backend.allreva.module.recruitment.rent.application.command.dto.RentUpdateRequest;
import com.backend.allreva.module.recruitment.rent.domain.value.BoardingType;
import com.backend.allreva.module.recruitment.rent.domain.value.RefundType;
import com.backend.allreva.module.recruitment.rent.domain.value.Route;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.instancio.Instancio;
import org.instancio.Model;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RentFixture {

    public static Model<RentRegisterRequest> rentRegisterRequestModel() {
        Route route = Route.builder()
                .boardingArea("서울")
                .dropOffArea("KSPO DOME")
                .time("09:00")
                .build();
        return Instancio.of(RentRegisterRequest.class)
                .set(field(RentRegisterRequest.class, "endDate"), LocalDate.of(2030, 11, 30))
                .set(field(RentRegisterRequest.class, "boardingType"), BoardingType.ROUND)
                .set(field(RentRegisterRequest.class, "upRoute"), route)
                .set(field(RentRegisterRequest.class, "downRoute"), route)
                .set(field(RentRegisterRequest.class, "maxPassenger"), 45)
                .set(field(RentRegisterRequest.class, "recruitmentCount"), 45)
                .set(field(RentRegisterRequest.class, "image"), new Image("https://example.com/rent.png"))
                .toModel();
    }

    public static Model<RentUpdateRequest> rentUpdateRequestModel() {
        return Instancio.of(RentUpdateRequest.class)
                .set(field(RentUpdateRequest.class, "endDate"), LocalDate.of(2030, 11, 30))
                .toModel();
    }

    public static Model<RentJoinRequest> rentJoinRequestModel() {
        return Instancio.of(RentJoinRequest.class)
                .set(field(RentJoinRequest.class, "passengerNum"), 1)
                .set(field(RentJoinRequest.class, "depositorName"), "홍길동")
                .set(field(RentJoinRequest.class, "depositorTime"), "10:00")
                .set(field(RentJoinRequest.class, "phone"), "010-1234-5678")
                .set(field(RentJoinRequest.class, "refundType"), RefundType.REFUND)
                .set(field(RentJoinRequest.class, "refundAccount"), "카카오뱅크 1234-5678")
                .toModel();
    }

    public static Model<RentJoinUpdateRequest> rentJoinUpdateRequestModel() {
        return Instancio.of(RentJoinUpdateRequest.class)
                .set(field(RentJoinUpdateRequest.class, "passengerNum"), 1)
                .set(field(RentJoinUpdateRequest.class, "depositorName"), "홍길동")
                .set(field(RentJoinUpdateRequest.class, "depositorTime"), "10:00")
                .set(field(RentJoinUpdateRequest.class, "phone"), "010-1234-5678")
                .set(field(RentJoinUpdateRequest.class, "refundType"), RefundType.REFUND)
                .set(field(RentJoinUpdateRequest.class, "refundAccount"), "카카오뱅크 1234-5678")
                .toModel();
    }

    public static Model<RentIdRequest> rentIdRequestModel() {
        return Instancio.of(RentIdRequest.class).toModel();
    }

    public static Model<RentJoinIdRequest> rentJoinIdRequestModel() {
        return Instancio.of(RentJoinIdRequest.class).toModel();
    }
}
