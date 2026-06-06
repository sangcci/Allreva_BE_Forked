package com.backend.allreva.recruitment.rent.domain;

import java.util.Optional;

public interface RentRepository {

    Optional<Rent> findById(Long id);

    Rent save(Rent rent);

    void delete(Rent rent);
}
