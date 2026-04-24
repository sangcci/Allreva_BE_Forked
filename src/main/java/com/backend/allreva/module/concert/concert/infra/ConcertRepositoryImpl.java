package com.backend.allreva.module.concert.concert.infra;

import static com.backend.allreva.module.concert.concert.domain.QConcert.concert;
import static com.backend.allreva.module.concert.place.domain.QConcertHall.concertHall;

import com.backend.allreva.module.concert.concert.application.dto.ConcertDateInfoResponse;
import com.backend.allreva.module.concert.concert.application.dto.ConcertDetailResponse;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.concert.infra.jpa.ConcertJpaRepository;
import com.backend.allreva.module.concert.place.application.dto.RelatedConcertResponse;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.infra.jpa.ConcertHallJpaRepository;
import com.backend.allreva.module.search.application.dto.ConcertThumbnail;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertJpaRepository jpa;
    private final ConcertHallJpaRepository concertHallJpa;
    private final JPAQueryFactory queryFactory;

    @Override
    public Concert save(final Concert concertEntity) {
        return jpa.save(concertEntity);
    }

    @Override
    public Optional<Concert> findById(final String concertCode) {
        return jpa.findById(concertCode);
    }

    @Override
    public List<Concert> findAll() {
        return jpa.findAll();
    }

    @Override
    public Optional<ConcertDateInfoResponse> findStartDateAndEndDateById(final String concertCode) {
        return jpa.findStartDateAndEndDateByConcertCode(concertCode);
    }

    @Override
    public ConcertDetailResponse findDetailById(final String concertCode) {
        return jpa.findById(concertCode)
                .map(concertEntity -> {
                    ConcertHall hall =
                            concertHallJpa.findById(concertEntity.getHallCode()).orElse(null);
                    return ConcertDetailResponse.from(concertEntity, hall);
                })
                .orElse(ConcertDetailResponse.EMPTY);
    }

    @Override
    public List<ConcertThumbnail> getConcertMainThumbnails() {
        return queryFactory
                .select(Projections.constructor(
                        ConcertThumbnail.class,
                        concert.poster.url,
                        concert.concertInfo.title,
                        concertHall.name,
                        concert.concertInfo.dateInfo.startDate,
                        concert.concertInfo.dateInfo.endDate,
                        concert.concertCode))
                .from(concert)
                .leftJoin(concertHall)
                .on(concert.hallCode.eq(concertHall.id))
                .where(concert.concertInfo.dateInfo.endDate.goe(LocalDate.now()))
                .orderBy(concert.concertInfo.dateInfo.startDate.asc())
                .limit(5)
                .fetch();
    }

    @Override
    public List<RelatedConcertResponse> findRelatedConcertsByHall(
            final String hallCode, final String lastConcertCode, final int pageSize) {
        return queryFactory
                .select(Projections.constructor(
                        RelatedConcertResponse.class,
                        concert.concertCode,
                        concert.concertInfo.title,
                        concert.concertInfo.dateInfo.startDate,
                        concert.concertInfo.dateInfo.endDate,
                        concert.poster.url))
                .from(concert)
                .where(eqHallCode(hallCode), ltConcertCode(lastConcertCode))
                .orderBy(concert.concertCode.desc())
                .limit(pageSize)
                .fetch();
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }

    @Override
    public void deleteAllInBatch() {
        jpa.deleteAllInBatch();
    }

    private BooleanExpression eqHallCode(final String hallCode) {
        return hallCode != null ? concert.hallCode.eq(hallCode) : null;
    }

    private BooleanExpression ltConcertCode(final String lastConcertCode) {
        return lastConcertCode != null ? concert.concertCode.lt(lastConcertCode) : null;
    }
}
