package com.backend.allreva.concert.place;

import com.backend.allreva.concert.place.query.implementation.ConcertHallFinderPort;
import com.backend.allreva.concert.place.query.model.ConcertHallDetailResult;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertHallFinderAdapter implements ConcertHallFinderPort {

    private final ConcertHallJpaRepository concertHallJpaRepository;

    @Override
    public Optional<ConcertHallDetailResult> findConcertHallDetail(final String hallCode) {
        return concertHallJpaRepository
                .findById(hallCode)
                .map(ConcertHallEntity::toDomain)
                .map(ConcertHallDetailResult::from);
    }
}
