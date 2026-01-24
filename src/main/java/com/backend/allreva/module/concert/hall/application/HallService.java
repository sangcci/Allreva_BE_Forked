package com.backend.allreva.module.concert.hall.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.concert.concert.infra.ConcertRepository;
import com.backend.allreva.module.concert.hall.application.dto.ConcertHallDetailResponse;
import com.backend.allreva.module.concert.hall.application.dto.RelatedConcertResponse;
import com.backend.allreva.module.concert.hall.domain.ConcertHall;
import com.backend.allreva.module.concert.hall.domain.ConcertHallRepository;
import com.backend.allreva.module.concert.hall.exception.ConcertHallErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class HallService {

    private final ConcertHallRepository concertHallRepository;
    private final ConcertRepository concertRepository;

    public ConcertHallDetailResponse findDetailByHallCode(final String hallCode) {
        return concertHallRepository.findDetailByHallCode(hallCode);
    }

    @Cacheable(cacheNames = "relatedConcert", key = "#hallCode + '_' + #lastId + '_' + #pageSize", unless = "#result == null", cacheManager = "relatedConcertCacheManager")
    public List<RelatedConcertResponse> getRelatedConcert(
            final String hallCode, final Long lastId, final Long lastViewCount, final int pageSize) {
        try {
            return concertRepository.findRelatedConcertsByHall(hallCode, lastId, lastViewCount, pageSize);
        } catch (Exception e) {
            throw new CustomException(ConcertHallErrorCode.RELATED_CONCERT_EXCEPTION);
        }
    }

    @Transactional
    public ConcertHall updateConcertHallStar(
            final String hallId,
            final int starDelta,
            final int countDelta) {
        ConcertHall concertHall = concertHallRepository.findByIdWithLock(hallId)
                .orElseThrow(() -> new CustomException(ConcertHallErrorCode.CONCERT_HALL_SEARCH_NOTFOUND));

        concertHall.updateStar(starDelta, countDelta);

        return concertHallRepository.save(concertHall);
    }
}
