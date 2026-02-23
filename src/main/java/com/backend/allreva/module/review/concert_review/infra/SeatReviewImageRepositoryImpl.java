package com.backend.allreva.module.review.concert_review.infra;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.backend.allreva.module.review.concert_review.domain.SeatReviewImage;
import com.backend.allreva.module.review.concert_review.domain.SeatReviewImageRepository;
import com.backend.allreva.module.review.concert_review.infra.jpa.SeatReviewImageJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SeatReviewImageRepositoryImpl implements SeatReviewImageRepository {

    private final SeatReviewImageJpaRepository jpa;

    @Override
    public List<SeatReviewImage> saveAll(final Iterable<SeatReviewImage> images) {
        return jpa.saveAll(images);
    }

    @Override
    public List<SeatReviewImage> findBySeatReviewId(final Long seatReviewId) {
        return jpa.findBySeatReviewId(seatReviewId);
    }

    @Override
    public void deleteAll(final Iterable<SeatReviewImage> images) {
        jpa.deleteAll(images);
    }
}
