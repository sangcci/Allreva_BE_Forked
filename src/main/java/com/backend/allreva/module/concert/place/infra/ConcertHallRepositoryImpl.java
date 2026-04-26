package com.backend.allreva.module.concert.place.infra;

import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import com.backend.allreva.module.concert.place.infra.jpa.ConcertHallJpaRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertHallRepositoryImpl implements ConcertHallRepository {

    private final ConcertHallJpaRepository jpa;

    @Override
    public ConcertHall save(final ConcertHall concertHallEntity) {
        return jpa.save(concertHallEntity);
    }

    @Override
    public Optional<ConcertHall> findById(final String hallCode) {
        return jpa.findById(hallCode);
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }

    @Override
    public List<String> findAllHallCodes() {
        return jpa.findAllHallCodes();
    }

    @Override
    public Set<String> findAllFacilityCodes() {
        return new HashSet<>(jpa.findAllFacilityCodes());
    }

    @Override
    public Set<String> findHallCodesByFacilityCode(final String facilityCode) {
        return new HashSet<>(jpa.findHallCodesByFacilityCode(facilityCode));
    }
}
