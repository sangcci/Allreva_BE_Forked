package com.backend.allreva.module.concert.place.application;

import com.backend.allreva.module.concert.place.application.dto.ConcertHallDetailResponse;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HallService {

    private final ConcertHallRepository concertHallRepository;

    @Transactional(readOnly = true)
    public ConcertHallDetailResponse findDetailByHallCode(final String hallCode) {
        return concertHallRepository.findDetailByHallCode(hallCode);
    }
}
