package com.backend.allreva.module.review.concert_review.infra.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.allreva.module.review.concert_review.domain.SeatReview;

public interface SeatReviewJpaRepository extends JpaRepository<SeatReview, Long> {

}
