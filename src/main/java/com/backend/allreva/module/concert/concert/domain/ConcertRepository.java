package com.backend.allreva.module.concert.concert.domain;

import java.util.List;
import java.util.Optional;

public interface ConcertRepository {

    Optional<Concert> findById(String concertCode);

    List<Concert> findAll();

    List<Concert> findAllByHallCode(String hallCode, String lastConcertCode, int pageSize);

    Concert save(Concert concert);

    void deleteAll();
}
