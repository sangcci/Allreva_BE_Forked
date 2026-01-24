package com.backend.allreva.module.concert.hall.infra;

import com.backend.allreva.module.concert.hall.application.dto.ConcertHallDetailResponse;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.backend.allreva.module.concert.hall.domain.QConcertHall.concertHall;

@RequiredArgsConstructor
@Repository
public class ConcertHallRepositoryImpl implements ConcertHallRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public ConcertHallDetailResponse findDetailByHallCode(final String hallCode) {
        return queryFactory.select(hallDetailProjections())
                .from(concertHall)
                .where(concertHall.id.eq(hallCode))
                .fetchFirst();
    }

    private static ConstructorExpression<ConcertHallDetailResponse> hallDetailProjections() {
        return Projections.constructor(ConcertHallDetailResponse.class,
                concertHall.name,
                concertHall.seatScale,
                concertHall.star,
                concertHall.convenienceInfo,
                concertHall.location
        );
    }
}
