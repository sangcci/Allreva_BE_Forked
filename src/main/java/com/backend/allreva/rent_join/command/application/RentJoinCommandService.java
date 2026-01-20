package com.backend.allreva.rent_join.command.application;

import com.backend.allreva.rent.command.domain.RentBoardingInfo;
import com.backend.allreva.rent.command.domain.RentRepository;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.rent.exception.RentErrorCode;
import com.backend.allreva.rent_join.command.application.request.RentJoinApplyRequest;
import com.backend.allreva.rent_join.command.application.request.RentJoinIdRequest;
import com.backend.allreva.rent_join.command.application.request.RentJoinUpdateRequest;
import com.backend.allreva.rent_join.command.domain.RentJoin;
import com.backend.allreva.rent_join.command.domain.RentJoinRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RentJoinCommandService {

    private final RentRepository rentRepository;
    private final RentJoinRepository rentJoinRepository;

    public Long applyRent(
            final RentJoinApplyRequest request,
            final Long memberId) {
        if (rentJoinRepository.exists(memberId, request.rentId(), request.boardingDate())) {
            throw new CustomException(RentErrorCode.RENT_JOIN_ALREADY_EXISTS);
        }

        RentBoardingInfo rentBoardingInfo = rentRepository
                .findByIdAndBoardingDate(request.rentId(), request.boardingDate())
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));
        rentBoardingInfo.addPassengerCount(request.passengerNum());

        RentJoin rentJoin = request.toEntity(memberId);
        RentJoin savedRentJoin = rentJoinRepository.save(rentJoin);
        return savedRentJoin.getId();
    }

    public void updateRentJoin(
            final RentJoinUpdateRequest request,
            final Long memberId) {
        RentJoin rentJoin = rentJoinRepository.findById(request.rentJoinId())
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_JOIN_NOT_FOUND));

        rentJoin.validateMine(memberId);

        rentJoin.updateRentJoin(request);
    }

    public void deleteRentJoin(
            final RentJoinIdRequest request,
            final Long memberId) {
        RentJoin rentJoin = rentJoinRepository.findById(request.rentJoinId())
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_JOIN_NOT_FOUND));

        rentJoin.validateMine(memberId);

        rentJoinRepository.delete(rentJoin);
    }
}
