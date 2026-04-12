package com.backend.allreva.module.recruitment.rent.infra;

import com.backend.allreva.module.recruitment.rent.domain.RentBoardingSlotRepository;
import com.backend.allreva.module.recruitment.rent.infra.jpa.RentBoardingSlotJpaRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RentBoardingSlotRepositoryImpl implements RentBoardingSlotRepository {

    private final RentBoardingSlotJpaRepository rentBoardingSlotJpaRepository;

    @Override
    public int incrementPassengerCount(final Long rentId, final LocalDate boardingDate, final int count) {
        return rentBoardingSlotJpaRepository.incrementPassengerCount(rentId, boardingDate, count);
    }
}
