package com.backend.allreva.recruitment.rent;

import com.backend.allreva.recruitment.rent.domain.Rent;
import com.backend.allreva.recruitment.rent.domain.RentRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RentRepositoryImpl implements RentRepository {

    private final RentJpaRepository rentJpaRepository;

    @Override
    public Optional<Rent> findById(final Long id) {
        return rentJpaRepository.findWithBoardingSlotsById(id).map(RentEntity::toDomain);
    }

    @Override
    public Rent save(final Rent rent) {
        RentEntity saved = rentJpaRepository.save(RentEntity.from(rent));
        return rentJpaRepository
                .findWithBoardingSlotsById(saved.getId())
                .orElseThrow()
                .toDomain();
    }

    @Override
    public void delete(final Rent rent) {
        rentJpaRepository.findById(rent.getId()).ifPresent(rentJpaRepository::delete);
    }
}
