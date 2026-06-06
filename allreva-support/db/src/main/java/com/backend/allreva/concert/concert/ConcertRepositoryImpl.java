package com.backend.allreva.concert.concert;

import com.backend.allreva.concert.concert.domain.Concert;
import com.backend.allreva.concert.concert.domain.ConcertRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertJpaRepository jpa;

    @Override
    public Optional<Concert> findById(final String concertCode) {
        return jpa.findById(concertCode).map(ConcertEntity::toDomain);
    }

    @Override
    public Concert save(final Concert concert) {
        return jpa.save(ConcertEntity.from(concert)).toDomain();
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }
}
