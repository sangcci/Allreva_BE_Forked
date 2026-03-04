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
    public Concert save(final Concert concert) {
        return jpa.save(concert);
    }

    @Override
    public Optional<Concert> findById(final Long id) {
        return jpa.findById(id);
    }

    @Override
    public boolean existsByCodeConcertCode(final String concertCode) {
        return jpa.existsByCodeConcertCode(concertCode);
    }

    @Override
    public Concert findByCodeConcertCode(final String concertCode) {
        return jpa.findByCodeConcertCode(concertCode);
    }

    @Override
    public Optional<ConcertDateInfoResponse> findStartDateAndEndDateById(final Long concertId) {
        return jpa.findStartDateAndEndDateById(concertId);
    }

    @Override
    public ConcertDetailResponse findDetailById(final Long concertId) {
        return jpa.findById(concertId)
                .map(concertEntity -> {
                    ConcertHall hall = concertHallJpa
                            .findById(concertEntity.getCode().getHallCode())
                            .orElse(null);
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
                        concert.id))
                .from(concert)
                .leftJoin(concertHall)
                .on(concert.code.hallCode.eq(concertHall.id))
                .where(concert.concertInfo.dateInfo.endDate.goe(LocalDate.now()))
                .orderBy(concert.concertInfo.dateInfo.startDate.asc())
                .limit(5)
                .fetch();
    }

    @Override
    public List<RelatedConcertResponse> findRelatedConcertsByHall(
            final String hallCode, final Long lastId, final Long lastViewCount, final int pageSize) {
        return queryFactory
                .select(Projections.constructor(
                        RelatedConcertResponse.class,
                        concert.id,
                        concert.concertInfo.title,
                        concert.concertInfo.dateInfo.startDate,
                        concert.concertInfo.dateInfo.endDate,
                        concert.poster.url,
                        concert.viewCount))
                .from(concert)
                .where(eqHallCode(hallCode), ltCursor(lastId, lastViewCount))
                .orderBy(concert.viewCount.desc(), concert.id.desc())
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
        return hallCode != null ? concert.code.hallCode.eq(hallCode) : null;
    }

    private BooleanExpression ltCursor(final Long lastId, final Long lastViewCount) {
        if (lastViewCount == null || lastId == null) {
            return null;
        }
        return concert.viewCount
                .lt(lastViewCount)
                .or(concert.viewCount.eq(lastViewCount).and(concert.id.lt(lastId)));
    }
}
