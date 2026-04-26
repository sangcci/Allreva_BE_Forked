package com.backend.allreva.module.concert.concert.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.concert.concert.application.dto.ConcertDetailResponse;
import com.backend.allreva.module.concert.concert.application.dto.RelatedConcertResponse;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.concert.exception.ConcertErrorCode;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import com.backend.allreva.module.concert.place.exception.ConcertHallErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final ConcertHallRepository concertHallRepository;

    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = "relatedConcert",
            key = "#hallCode + '_' + #lastConcertCode + '_' + #pageSize",
            unless = "#result == null",
            cacheManager = "relatedConcertCacheManager")
    public List<RelatedConcertResponse> getRelatedConcertsByHallCode(
            final String hallCode, final String lastConcertCode, final int pageSize) {
        return concertRepository.findAllByHallCode(hallCode, lastConcertCode, pageSize).stream()
                .map(RelatedConcertResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConcertDetailResponse findDetailById(final String concertCode) {
        Concert concert = concertRepository
                .findById(concertCode)
                .orElseThrow(() -> new CustomException(ConcertErrorCode.CONCERT_NOT_FOUND));
        ConcertHall hall = concertHallRepository
                .findById(concert.getHallCode())
                .orElseThrow(() -> new CustomException(ConcertHallErrorCode.CONCERT_HALL_NOT_FOUND));
        return ConcertDetailResponse.from(concert, hall);
    }
}
