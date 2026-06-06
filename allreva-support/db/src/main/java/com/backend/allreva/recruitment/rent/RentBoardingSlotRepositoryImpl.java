package com.backend.allreva.recruitment.rent;

import com.backend.allreva.recruitment.rent.domain.RentBoardingSlotRepository;
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

    @Override
    public int decrementPassengerCount(final Long rentId, final LocalDate boardingDate, final int count) {
        return rentBoardingSlotJpaRepository.decrementPassengerCount(rentId, boardingDate, count);
    }
}
