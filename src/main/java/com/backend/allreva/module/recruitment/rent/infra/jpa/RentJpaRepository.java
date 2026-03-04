package com.backend.allreva.module.recruitment.rent.infra.jpa;

import com.backend.allreva.module.recruitment.rent.domain.Rent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentJpaRepository extends JpaRepository<Rent, Long> {

    Optional<Rent> findByIdAndMemberId(Long id, Long memberId);
}
