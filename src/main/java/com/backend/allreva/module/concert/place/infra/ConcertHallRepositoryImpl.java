package com.backend.allreva.module.concert.place.infra;

import static com.backend.allreva.module.concert.place.domain.QConcertHall.concertHall;

import com.backend.allreva.module.concert.place.application.dto.ConcertHallDetailResponse;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import com.backend.allreva.module.concert.place.infra.jpa.ConcertHallJpaRepository;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
    private final JPAQueryFactory queryFactory;

    @Override
    public ConcertHall save(final ConcertHall concertHallEntity) {
        return jpa.save(concertHallEntity);
    }

    @Override
    public Optional<ConcertHall> findByHallCodeWithLock(final String hallCode) {
        return jpa.findByHallCodeWithLock(hallCode);
    }

    @Override
    public Optional<ConcertHall> findByHallCode(final String hallCode) {
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

    @Override
    public ConcertHallDetailResponse findDetailByHallCode(final String hallCode) {
        return queryFactory
                .select(hallDetailProjections())
                .from(concertHall)
                .where(concertHall.hallCode.eq(hallCode))
                .fetchFirst();
    }

    private static ConstructorExpression<ConcertHallDetailResponse> hallDetailProjections() {
        return Projections.constructor(
                ConcertHallDetailResponse.class,
                concertHall.name,
                concertHall.seatScale,
                concertHall.convenienceInfo,
                concertHall.location);
    }
}
