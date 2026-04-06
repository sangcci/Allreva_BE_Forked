package com.backend.allreva.module.recruitment.rent.domain;

import com.backend.allreva.module.recruitment.rent.application.dto.SortType;
import com.backend.allreva.module.recruitment.rent.domain.value.Region;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RentRepository {

    Optional<Rent> findById(Long id);

    Optional<Rent> findByIdAndMemberId(Long rentId, Long memberId);

    List<Rent> findAll(Region region, SortType sortType, LocalDate lastEndDate, Long lastId, int pageSize);

    List<Rent> findAllByMemberId(Long memberId, Long lastId, int pageSize);

    List<Rent> findAllByIds(List<Long> ids);

    Rent save(Rent rent);

    void delete(Rent rent);
}
