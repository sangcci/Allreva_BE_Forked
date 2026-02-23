package com.backend.allreva.module.review.concert_review.infra;

import static com.backend.allreva.module.review.concert_review.domain.QSeatReview.seatReview;
import static com.backend.allreva.module.review.concert_review.domain.QSeatReviewImage.seatReviewImage;
import static com.backend.allreva.module.member.domain.QMember.member;
import static com.querydsl.core.types.Projections.list;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.allreva.module.review.concert_review.application.dto.SeatReviewResponse;
import com.backend.allreva.module.review.concert_review.application.dto.SeatReviewSearchCondition;
import com.backend.allreva.module.review.concert_review.application.dto.SortType;
import com.backend.allreva.module.review.concert_review.domain.SeatReview;
import com.backend.allreva.module.review.concert_review.domain.SeatReviewRepository;
import com.backend.allreva.module.review.concert_review.infra.jpa.SeatReviewJpaRepository;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SeatReviewRepositoryImpl implements SeatReviewRepository {

    private final SeatReviewJpaRepository jpa;
    private final JPAQueryFactory queryFactory;

    @Override
    public SeatReview save(final SeatReview review) {
        return jpa.save(review);
    }

    @Override
    public Optional<SeatReview> findById(final Long id) {
        return jpa.findById(id);
    }

    @Override
    public void delete(final SeatReview review) {
        jpa.delete(review);
    }

    @Override
    public List<SeatReviewResponse> findReviewsWithNoOffset(
            final SeatReviewSearchCondition condition,
            final Long currentMemberId) {
        return queryFactory
                .select(Projections.constructor(SeatReviewResponse.class,
                        seatReview.id,
                        seatReview.seat,
                        seatReview.content,
                        seatReview.star,
                        seatReview.memberId,
                        seatReview.hallId,
                        seatReview.viewDate,
                        seatReview.createdAt,
                        list(JPAExpressions
                                .select(seatReviewImage.url)
                                .from(seatReviewImage)
                                .where(seatReviewImage.seatReviewId.eq(seatReview.id))
                                .orderBy(seatReviewImage.orderNum.asc())),
                        member.memberInfo.profileImageUrl,
                        member.memberInfo.nickname,
                        seatReview.memberId.eq(condition.memberId()),
                        seatReview.concertTitle
                ))
                .from(seatReview)
                .join(member).on(seatReview.memberId.eq(member.id))
                .where(
                        createPaginationCondition(
                                condition.lastId(),
                                condition.lastCreatedAt(),
                                condition.sortType()
                        ),
                        seatReview.hallId.eq(condition.hallId())
                )
                .orderBy(
                        getOrderSpecifier(condition.sortType()),
                        seatReview.id.desc()
                )
                .limit(condition.size())
                .fetch();
    }

    private BooleanExpression createPaginationCondition(
            final Long lastId,
            final LocalDateTime lastCreatedAt,
            final SortType sortType) {
        if (lastId == null || lastCreatedAt == null) return null;

        return sortType == SortType.CREATED_ASC
                ? seatReview.createdAt.gt(lastCreatedAt)
                        .or(seatReview.createdAt.eq(lastCreatedAt).and(seatReview.id.lt(lastId)))
                : seatReview.createdAt.lt(lastCreatedAt)
                        .or(seatReview.createdAt.eq(lastCreatedAt).and(seatReview.id.lt(lastId)));
    }

    private OrderSpecifier<?> getOrderSpecifier(final SortType sortType) {
        return sortType == SortType.CREATED_ASC
                ? seatReview.createdAt.asc()
                : seatReview.createdAt.desc();
    }
}
