package com.backend.allreva.module.recruitment.rent.infra;

import com.backend.allreva.module.recruitment.rent.domain.RentBoardingSlot;
import com.backend.allreva.module.recruitment.rent.domain.RentBoardingSlotRepository;
import com.backend.allreva.module.recruitment.rent.infra.jpa.RentBoardingSlotJpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RentBoardingSlotRepositoryImpl implements RentBoardingSlotRepository {

    private final RentBoardingSlotJpaRepository rentBoardingSlotJpaRepository;

    @Override
    public Optional<RentBoardingSlot> findById(final Long id) {
        return rentBoardingSlotJpaRepository.findById(id);
    }

    @Override
    public Optional<RentBoardingSlot> findByRentIdAndDate(final Long rentId, final LocalDate date) {
        return rentBoardingSlotJpaRepository.findByRentIdAndDate(rentId, date);
    }

    @Override
    public List<RentBoardingSlot> findAllByRentId(final Long rentId) {
        return rentBoardingSlotJpaRepository.findAllByRentId(rentId);
    }

    @Override
    public RentBoardingSlot save(final RentBoardingSlot slot) {
        return rentBoardingSlotJpaRepository.save(slot);
    }

    @Override
    public int incrementPassengerCount(final Long rentId, final LocalDate boardingDate, final int count) {
        return rentBoardingSlotJpaRepository.incrementPassengerCount(rentId, boardingDate, count);
    }

    @Override
    public void deleteAllByRentId(final Long rentId) {
        rentBoardingSlotJpaRepository.deleteAllByRentId(rentId);
    }
}
