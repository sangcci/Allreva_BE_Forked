package com.backend.allreva.module.recruitment.rent.infra;

import static com.backend.allreva.module.recruitment.rent.domain.QRent.rent;

import com.backend.allreva.module.recruitment.rent.application.dto.SortType;
import com.backend.allreva.module.recruitment.rent.domain.Rent;
import com.backend.allreva.module.recruitment.rent.domain.RentRepository;
import com.backend.allreva.module.recruitment.rent.infra.jpa.RentJpaRepository;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RentRepositoryImpl implements RentRepository {

    private final RentJpaRepository rentJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Rent> findById(final Long id) {
        return rentJpaRepository.findById(id);
    }

    @Override
    public Optional<Rent> findByIdAndMemberId(final Long id, final Long memberId) {
        return rentJpaRepository.findByIdAndMemberId(id, memberId);
    }

    @Override
    public Rent save(final Rent rentEntity) {
        return rentJpaRepository.save(rentEntity);
    }

    @Override
    public void delete(final Rent rentEntity) {
        rentJpaRepository.delete(rentEntity);
    }

    @Override
    public List<Rent> findAll(
            final String region,
            final SortType sortType,
            final LocalDate lastEndDate,
            final Long lastId,
            final int pageSize) {
        return queryFactory
                .selectFrom(rent)
                .where(
                        rent.isClosed.eq(false),
                        getRegionCondition(region),
                        getPagingCondition(sortType, lastId, lastEndDate))
                .groupBy(rent.id)
                .orderBy(orderSpecifiers(sortType))
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<Rent> findAllByIds(final List<Long> ids) {
        return rentJpaRepository.findAllById(ids);
    }

    @Override
    public List<Rent> findAllByMemberId(final Long memberId, final Long lastId, final int pageSize) {
        return queryFactory
                .selectFrom(rent)
                .where(rent.memberId.eq(memberId), getPagingCondition(SortType.LATEST, lastId, null))
                .orderBy(orderSpecifiers(SortType.LATEST))
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression getRegionCondition(final String region) {
        return region == null ? null : rent.region.eq(region);
    }

    private BooleanExpression getPagingCondition(
            final SortType sortType, final Long lastId, final LocalDate lastEndDate) {
        if (lastId == null && lastEndDate == null) {
            return null;
        }

        switch (sortType) {
            case CLOSING -> {
                return (rent.endDate.gt(lastEndDate))
                        .or(rent.endDate.eq(lastEndDate).and(rent.id.gt(lastId)));
            }
            case OLDEST -> {
                return rent.id.gt(lastId);
            }
            default -> {
                return rent.id.lt(lastId);
            }
        }
    }

    private OrderSpecifier<?>[] orderSpecifiers(final SortType sortType) {
        switch (sortType) {
            case CLOSING -> {
                return new OrderSpecifier[] {rent.endDate.asc(), rent.id.asc()};
            }
            case OLDEST -> {
                return new OrderSpecifier[] {rent.id.asc()};
            }
            default -> {
                return new OrderSpecifier[] {rent.id.desc()};
            }
        }
    }
}
