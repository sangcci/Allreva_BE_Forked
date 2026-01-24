package com.backend.allreva.module.concert.concert.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.allreva.module.concert.concert.infra.ConcertRepository;
import com.backend.allreva.module.concert.concert.application.dto.ConcertDetailResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ConcertService {

    private final ConcertRepository concertRepository;

    @Transactional
    public ConcertDetailResponse findDetailById(final Long concertId) {
        // Increment view count
        concertRepository.findById(concertId)
                .ifPresent(concert -> concert.addViewCount(1));
        return concertRepository.findDetailById(concertId);
    }

    @Transactional
    public void increaseViewCount(final Long concertId) {
        concertRepository.findById(concertId)
                .ifPresent(concert -> concert.addViewCount(1));
    }
}
