package com.backend.allreva.module.recruitment.rent.domain;

import java.time.LocalDate;

public interface RentBoardingSlotRepository {

    int incrementPassengerCount(Long rentId, LocalDate boardingDate, int count);
}
