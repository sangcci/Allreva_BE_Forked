package com.backend.allreva.module.concert.place.infra.jpa;

import com.backend.allreva.module.concert.place.domain.ConcertHall;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface ConcertHallJpaRepository extends JpaRepository<ConcertHall, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ConcertHall c WHERE c.id = :hallId")
    Optional<ConcertHall> findByIdWithLock(String hallId);
}
