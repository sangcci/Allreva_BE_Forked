package com.backend.allreva.module.concert.place.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.place.application.dto.ConcertHallDetailResponse;
import com.backend.allreva.module.concert.place.application.dto.RelatedConcertResponse;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import com.backend.allreva.module.concert.place.exception.ConcertHallErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HallService {

    private final ConcertHallRepository concertHallRepository;
    private final ConcertRepository concertRepository;

    @Transactional(readOnly = true)
    public ConcertHallDetailResponse findDetailByHallCode(final String hallCode) {
        return concertHallRepository.findDetailByHallCode(hallCode);
    }

    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = "relatedConcert",
            key = "#hallCode + '_' + #lastId + '_' + #pageSize",
            unless = "#result == null",
            cacheManager = "relatedConcertCacheManager")
    public List<RelatedConcertResponse> getRelatedConcert(
            final String hallCode, final Long lastId, final int pageSize) {
        try {
            return concertRepository.findRelatedConcertsByHall(hallCode, lastId, pageSize);
        } catch (Exception e) {
            throw new CustomException(ConcertHallErrorCode.RELATED_CONCERT_EXCEPTION);
        }
    }

    @Transactional
    public ConcertHall updateConcertHallStar(final String hallId, final int starDelta, final int countDelta) {
        ConcertHall concertHall = concertHallRepository
                .findByIdWithLock(hallId)
                .orElseThrow(() -> new CustomException(ConcertHallErrorCode.CONCERT_HALL_SEARCH_NOTFOUND));

        concertHall.updateStar(starDelta, countDelta);

        return concertHallRepository.save(concertHall);
    }
}
