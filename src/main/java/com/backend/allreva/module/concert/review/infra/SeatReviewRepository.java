package com.backend.allreva.module.concert.review.infra;

import com.backend.allreva.module.concert.review.domain.SeatReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatReviewRepository extends
        JpaRepository<SeatReview, Long> , SeatReviewRepositoryCustom{
}
