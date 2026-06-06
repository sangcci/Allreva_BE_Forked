package com.backend.allreva.recruitment.rent.command.implementation;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.recruitment.rent.domain.Rent;
import com.backend.allreva.recruitment.rent.domain.RentErrorCode;
import com.backend.allreva.recruitment.rent.domain.RentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentReader {

    private final RentRepository rentRepository;

    public Rent get(final Long id) {
        return rentRepository.findById(id).orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));
    }
}
