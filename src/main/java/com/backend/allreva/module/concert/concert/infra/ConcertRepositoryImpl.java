package com.backend.allreva.module.concert.concert.infra;

import static com.backend.allreva.module.concert.concert.domain.QConcert.concert;

import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.concert.infra.jpa.ConcertJpaRepository;
import com.backend.allreva.module.concert.place.application.dto.RelatedConcertResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertJpaRepository jpa;
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Concert> findById(final String concertCode) {
        return jpa.findById(concertCode);
    }

    @Override
    public List<Concert> findAll() {
        return jpa.findAll();
    }

    @Override
    public List<RelatedConcertResponse> findRelatedConcertsByHall(
            final String hallCode, final String lastConcertCode, final int pageSize) {
        return queryFactory
                .selectFrom(concert)
                .where(eqHallCode(hallCode), ltConcertCode(lastConcertCode))
                .orderBy(concert.concertCode.desc())
                .limit(pageSize)
                .fetch()
                .stream()
                .map(RelatedConcertResponse::from)
                .toList();
    }

    @Override
    public Concert save(final Concert concertEntity) {
        return jpa.save(concertEntity);
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }

    private BooleanExpression eqHallCode(final String hallCode) {
        return hallCode != null ? concert.hallCode.eq(hallCode) : null;
    }

    private BooleanExpression ltConcertCode(final String lastConcertCode) {
        return lastConcertCode != null ? concert.concertCode.lt(lastConcertCode) : null;
    }
}
