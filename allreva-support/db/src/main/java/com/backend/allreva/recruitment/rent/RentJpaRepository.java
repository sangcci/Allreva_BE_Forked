package com.backend.allreva.recruitment.rent;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentJpaRepository extends JpaRepository<RentEntity, Long> {

    @EntityGraph(attributePaths = "boardingSlots")
    Optional<RentEntity> findWithBoardingSlotsById(Long id);

    @EntityGraph(attributePaths = "boardingSlots")
    List<RentEntity> findAllByIdIn(List<Long> ids);
}
