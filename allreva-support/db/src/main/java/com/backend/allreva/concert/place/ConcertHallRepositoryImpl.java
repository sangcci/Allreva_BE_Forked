package com.backend.allreva.concert.place;

import com.backend.allreva.concert.place.domain.ConcertHall;
import com.backend.allreva.concert.place.domain.ConcertHallRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertHallRepositoryImpl implements ConcertHallRepository {

    private final ConcertHallJpaRepository jpa;

    @Override
    public Optional<ConcertHall> findById(final String hallCode) {
        return jpa.findById(hallCode).map(ConcertHallEntity::toDomain);
    }

    @Override
    public ConcertHall save(final ConcertHall concertHall) {
        return jpa.save(ConcertHallEntity.from(concertHall)).toDomain();
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }
}
