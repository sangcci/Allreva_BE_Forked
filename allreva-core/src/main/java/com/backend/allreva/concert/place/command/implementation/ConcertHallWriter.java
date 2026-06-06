package com.backend.allreva.concert.place.command.implementation;

import com.backend.allreva.concert.place.domain.ConcertHall;
import com.backend.allreva.concert.place.domain.ConcertHallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcertHallWriter {

    private final ConcertHallRepository concertHallRepository;

    public ConcertHall upsert(final ConcertHall concertHall) {
        return concertHallRepository.save(concertHall);
    }
}
