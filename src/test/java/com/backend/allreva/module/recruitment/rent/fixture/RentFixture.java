package com.backend.allreva.module.recruitment.rent.fixture;

import static org.instancio.Select.field;

import com.backend.allreva.module.recruitment.rent.application.dto.RentIdRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinIdRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinUpdateRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentRegisterRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentUpdateRequest;
import com.backend.allreva.module.recruitment.rent.domain.value.RefundType;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.instancio.Instancio;
import org.instancio.Model;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RentFixture {

    public static Model<RentRegisterRequest> rentRegisterRequestModel() {
        return Instancio.of(RentRegisterRequest.class)
                .set(field(RentRegisterRequest.class, "endDate"), LocalDate.of(2030, 11, 30))
                .toModel();
    }

    public static Model<RentUpdateRequest> rentUpdateRequestModel() {
        return Instancio.of(RentUpdateRequest.class)
                .set(field(RentUpdateRequest.class, "endDate"), LocalDate.of(2030, 11, 30))
                .toModel();
    }

    public static Model<RentJoinRequest> rentJoinRequestModel() {
        return Instancio.of(RentJoinRequest.class)
                .set(field(RentJoinRequest.class, "phone"), "010-1234-5678")
                .set(field(RentJoinRequest.class, "refundType"), RefundType.REFUND)
                .toModel();
    }

    public static Model<RentJoinUpdateRequest> rentJoinUpdateRequestModel() {
        return Instancio.of(RentJoinUpdateRequest.class)
                .set(field(RentJoinUpdateRequest.class, "phone"), "010-1234-5678")
                .set(field(RentJoinUpdateRequest.class, "refundType"), RefundType.REFUND)
                .toModel();
    }

    public static Model<RentIdRequest> rentIdRequestModel() {
        return Instancio.of(RentIdRequest.class).toModel();
    }

    public static Model<RentJoinIdRequest> rentJoinIdRequestModel() {
        return Instancio.of(RentJoinIdRequest.class).toModel();
    }
}
