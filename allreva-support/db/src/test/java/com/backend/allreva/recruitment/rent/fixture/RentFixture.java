package com.backend.allreva.recruitment.rent.fixture;

import static org.instancio.Select.field;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.recruitment.rent.domain.BoardingType;
import com.backend.allreva.recruitment.rent.domain.Bus;
import com.backend.allreva.recruitment.rent.domain.BusSize;
import com.backend.allreva.recruitment.rent.domain.BusType;
import com.backend.allreva.recruitment.rent.domain.Depositor;
import com.backend.allreva.recruitment.rent.domain.RefundType;
import com.backend.allreva.recruitment.rent.domain.Rent;
import com.backend.allreva.recruitment.rent.domain.RentBoardingSlot;
import com.backend.allreva.recruitment.rent.domain.RentParticipant;
import com.backend.allreva.recruitment.rent.domain.Route;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.instancio.Instancio;
import org.instancio.Model;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RentFixture {

    public static final String TITLE = "차대절";
    public static final String IMAGE_URL = "rent.jpg";
    public static final LocalDate BOARDING_DATE = LocalDate.of(2030, 1, 2);

    public static Model<Rent> rentModel() {
        Route upRoute = Route.builder()
                .boardingArea("서울역")
                .dropOffArea("공연장")
                .time("10:00")
                .build();
        Route downRoute = Route.builder()
                .boardingArea("공연장")
                .dropOffArea("서울역")
                .time("22:00")
                .build();
        Bus bus = Bus.builder()
                .busSize(BusSize.LARGE)
                .busType(BusType.PREMIUM)
                .maxPassenger(45)
                .build();

        return Instancio.of(Rent.class)
                .ignore(field(Rent.class, "id"))
                .set(field(Rent.class, "memberId"), 1L)
                .set(field(Rent.class, "concertCode"), "PF_TEST_001")
                .set(field(Rent.class, "title"), TITLE)
                .set(field(Rent.class, "image"), new Image(IMAGE_URL))
                .set(field(Rent.class, "region"), "서울")
                .set(field(Rent.class, "boardingType"), BoardingType.ROUND)
                .set(field(Rent.class, "upRoute"), upRoute)
                .set(field(Rent.class, "downRoute"), downRoute)
                .set(field(Rent.class, "bus"), bus)
                .set(field(Rent.class, "price"), 50000)
                .set(field(Rent.class, "endDate"), LocalDate.of(2030, 1, 1))
                .set(field(Rent.class, "information"), "안내")
                .set(field(Rent.class, "closed"), false)
                .set(field(Rent.class, "boardingSlots"), List.of(RentBoardingSlot.open(BOARDING_DATE, 45)))
                .toModel();
    }

    public static Model<RentParticipant> rentParticipantModel(final Long rentId) {
        return Instancio.of(RentParticipant.class)
                .ignore(field(RentParticipant.class, "id"))
                .set(field(RentParticipant.class, "rentId"), rentId)
                .set(field(RentParticipant.class, "memberId"), 2L)
                .set(field(Depositor.class, "depositorName"), "입금자")
                .set(field(Depositor.class, "depositorTime"), "12:30")
                .set(field(Depositor.class, "phone"), "010-1234-5678")
                .set(field(RentParticipant.class, "passengerNum"), 2)
                .set(field(RentParticipant.class, "refundType"), RefundType.REFUND)
                .set(field(RentParticipant.class, "refundAccount"), "은행 123")
                .set(field(RentParticipant.class, "boardingDate"), BOARDING_DATE)
                .toModel();
    }

    public static Rent createRent() {
        return Instancio.create(rentModel());
    }

    public static RentParticipant createRentParticipant(final Long rentId) {
        return Instancio.create(rentParticipantModel(rentId));
    }
}
