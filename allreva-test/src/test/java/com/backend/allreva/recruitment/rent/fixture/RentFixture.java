package com.backend.allreva.recruitment.rent.fixture;

import static org.instancio.Select.field;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.recruitment.rent.command.input.RentIdCommand;
import com.backend.allreva.recruitment.rent.command.input.RentJoinCommand;
import com.backend.allreva.recruitment.rent.command.input.RentJoinIdCommand;
import com.backend.allreva.recruitment.rent.command.input.RentJoinUpdateCommand;
import com.backend.allreva.recruitment.rent.command.input.RentRegisterCommand;
import com.backend.allreva.recruitment.rent.command.input.RentUpdateCommand;
import com.backend.allreva.recruitment.rent.domain.BoardingType;
import com.backend.allreva.recruitment.rent.domain.RefundType;
import com.backend.allreva.recruitment.rent.domain.Route;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.instancio.Instancio;
import org.instancio.Model;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RentFixture {

    public static Model<RentRegisterCommand> rentRegisterRequestModel() {
        Route route = Route.builder()
                .boardingArea("서울")
                .dropOffArea("KSPO DOME")
                .time("09:00")
                .build();
        return Instancio.of(RentRegisterCommand.class)
                .set(field(RentRegisterCommand.class, "endDate"), LocalDate.of(2030, 11, 30))
                .set(field(RentRegisterCommand.class, "boardingType"), BoardingType.ROUND)
                .set(field(RentRegisterCommand.class, "upRoute"), route)
                .set(field(RentRegisterCommand.class, "downRoute"), route)
                .set(field(RentRegisterCommand.class, "maxPassenger"), 45)
                .set(field(RentRegisterCommand.class, "recruitmentCount"), 45)
                .set(field(RentRegisterCommand.class, "image"), new Image("https://example.com/rent.png"))
                .toModel();
    }

    public static Model<RentUpdateCommand> rentUpdateRequestModel() {
        return Instancio.of(RentUpdateCommand.class)
                .set(field(RentUpdateCommand.class, "endDate"), LocalDate.of(2030, 11, 30))
                .toModel();
    }

    public static Model<RentJoinCommand> rentJoinRequestModel() {
        return Instancio.of(RentJoinCommand.class)
                .set(field(RentJoinCommand.class, "passengerNum"), 1)
                .set(field(RentJoinCommand.class, "depositorName"), "홍길동")
                .set(field(RentJoinCommand.class, "depositorTime"), "10:00")
                .set(field(RentJoinCommand.class, "phone"), "010-1234-5678")
                .set(field(RentJoinCommand.class, "refundType"), RefundType.REFUND)
                .set(field(RentJoinCommand.class, "refundAccount"), "카카오뱅크 1234-5678")
                .toModel();
    }

    public static Model<RentJoinUpdateCommand> rentJoinUpdateRequestModel() {
        return Instancio.of(RentJoinUpdateCommand.class)
                .set(field(RentJoinUpdateCommand.class, "passengerNum"), 1)
                .set(field(RentJoinUpdateCommand.class, "depositorName"), "홍길동")
                .set(field(RentJoinUpdateCommand.class, "depositorTime"), "10:00")
                .set(field(RentJoinUpdateCommand.class, "phone"), "010-1234-5678")
                .set(field(RentJoinUpdateCommand.class, "refundType"), RefundType.REFUND)
                .set(field(RentJoinUpdateCommand.class, "refundAccount"), "카카오뱅크 1234-5678")
                .toModel();
    }

    public static Model<RentIdCommand> rentIdRequestModel() {
        return Instancio.of(RentIdCommand.class).toModel();
    }

    public static Model<RentJoinIdCommand> rentJoinIdRequestModel() {
        return Instancio.of(RentJoinIdCommand.class).toModel();
    }
}
