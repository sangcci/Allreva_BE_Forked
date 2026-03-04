package com.backend.allreva.module.review.concert_review.infra.jpa;

import com.backend.allreva.module.review.concert_review.domain.SeatReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatReviewJpaRepository extends JpaRepository<SeatReview, Long> {}
