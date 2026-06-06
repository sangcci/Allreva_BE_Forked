package com.backend.allreva.concert.concert.domain;

import java.util.Optional;

public interface ConcertRepository {

    Optional<Concert> findById(String concertCode);

    Concert save(Concert concert);

    void deleteAll();
}
