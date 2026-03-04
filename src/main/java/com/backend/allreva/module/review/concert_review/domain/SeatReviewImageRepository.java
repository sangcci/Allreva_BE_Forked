package com.backend.allreva.module.review.concert_review.domain;

import java.util.List;

public interface SeatReviewImageRepository {

    List<SeatReviewImage> saveAll(Iterable<SeatReviewImage> images);

    List<SeatReviewImage> findBySeatReviewId(Long seatReviewId);

    void deleteAll(Iterable<SeatReviewImage> images);
}
