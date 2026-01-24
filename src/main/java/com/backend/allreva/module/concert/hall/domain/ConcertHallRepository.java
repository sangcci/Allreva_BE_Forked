package com.backend.allreva.module.concert.hall.domain;

import com.backend.allreva.module.concert.hall.infra.ConcertHallRepositoryCustom;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ConcertHallRepository extends JpaRepository<ConcertHall, String>, ConcertHallRepositoryCustom {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ConcertHall c WHERE c.id = :hallId")
    Optional<ConcertHall> findByIdWithLock(String hallId);
}
