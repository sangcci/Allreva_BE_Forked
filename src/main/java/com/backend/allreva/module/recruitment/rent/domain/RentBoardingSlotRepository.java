package com.backend.allreva.module.recruitment.rent.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RentBoardingSlotRepository {

    Optional<RentBoardingSlot> findById(Long id);

    Optional<RentBoardingSlot> findByRentIdAndDate(Long rentId, LocalDate date);

    List<RentBoardingSlot> findAllByRentId(Long rentId);

    RentBoardingSlot save(RentBoardingSlot slot);

    int incrementPassengerCount(Long rentId, LocalDate boardingDate, int count);

    void deleteAllByRentId(Long rentId);
}
