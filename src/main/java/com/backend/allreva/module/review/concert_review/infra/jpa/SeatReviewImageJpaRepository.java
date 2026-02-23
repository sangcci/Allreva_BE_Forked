package com.backend.allreva.module.review.concert_review.infra.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.allreva.module.review.concert_review.domain.SeatReviewImage;

public interface SeatReviewImageJpaRepository extends JpaRepository<SeatReviewImage, Long> {

    List<SeatReviewImage> findBySeatReviewId(Long seatReviewId);
}
