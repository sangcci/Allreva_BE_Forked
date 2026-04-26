package com.backend.allreva.module.concert.concert.application;

import com.backend.allreva.module.concert.concert.application.dto.ConcertDetailResponse;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final ConcertHallRepository concertHallRepository;

    @Transactional(readOnly = true)
    public ConcertDetailResponse findDetailById(final String concertCode) {
        return concertRepository
                .findById(concertCode)
                .map(concert -> {
                    ConcertHall hall = concertHallRepository
                            .findByHallCode(concert.getHallCode())
                            .orElse(null);
                    return ConcertDetailResponse.from(concert, hall);
                })
                .orElse(ConcertDetailResponse.EMPTY);
    }
}
