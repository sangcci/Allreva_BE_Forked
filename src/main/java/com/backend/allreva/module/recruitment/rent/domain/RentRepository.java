package com.backend.allreva.module.recruitment.rent.domain;

import com.backend.allreva.module.recruitment.rent.application.dto.RentAdminSummaryResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentDetailResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentSummaryResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.SortType;
import com.backend.allreva.module.recruitment.rent.domain.value.Region;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RentRepository {

    // for command
    Optional<Rent> findById(Long id);

    Optional<Rent> findByIdAndMemberId(Long rentId, Long memberId);

    Optional<RentBoardingInfo> findByIdAndBoardingDate(Long rentId, LocalDate date);

    Rent save(Rent rent);

    void deleteBoardingInfoAllByRentId(Long rentId);

    void delete(Rent rent);

    // for query
    List<RentSummaryResponse> findRentSummaries(
            Region region, SortType sortType, LocalDate lastEndDate, Long lastId, int pageSize);

    Optional<RentDetailResponse> findRentDetail(Long rentId);

    List<RentAdminSummaryResponse> findRentAdminSummaries(Long memberId, Long lastId, int pageSize);
}
