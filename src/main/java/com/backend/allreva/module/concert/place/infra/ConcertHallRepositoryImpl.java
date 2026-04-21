package com.backend.allreva.module.concert.place.infra;

import static com.backend.allreva.module.concert.place.domain.QConcertHall.concertHall;

import com.backend.allreva.module.concert.place.application.dto.ConcertHallDetailResponse;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import com.backend.allreva.module.concert.place.infra.jpa.ConcertHallJpaRepository;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
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
    public Optional<ConcertHall> findByIdWithLock(final String hallId) {
        return jpa.findByIdWithLock(hallId);
    }

    @Override
    public Optional<ConcertHall> findById(final String id) {
        return jpa.findById(id);
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }

    @Override
    public List<String> findAllIds() {
        return jpa.findAllIds();
    }

    @Override
    public ConcertHallDetailResponse findDetailByHallCode(final String hallCode) {
        return queryFactory
                .select(hallDetailProjections())
                .from(concertHall)
                .where(concertHall.id.eq(hallCode))
                .fetchFirst();
    }

    private static ConstructorExpression<ConcertHallDetailResponse> hallDetailProjections() {
        return Projections.constructor(
                ConcertHallDetailResponse.class,
                concertHall.name,
                concertHall.seatScale,
                concertHall.star,
                concertHall.convenienceInfo,
                concertHall.location);
    }
}
