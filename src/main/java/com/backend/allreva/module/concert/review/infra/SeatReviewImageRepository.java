package com.backend.allreva.module.concert.review.infra;

import com.backend.allreva.module.concert.review.domain.SeatReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatReviewImageRepository extends JpaRepository<SeatReviewImage, Long> {
    List<SeatReviewImage> findBySeatReviewId(Long id);
}
