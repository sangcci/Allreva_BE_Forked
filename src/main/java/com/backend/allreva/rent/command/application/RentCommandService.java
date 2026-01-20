package com.backend.allreva.rent.command.application;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.backend.allreva.common.event.Events;
import com.backend.allreva.common.storage.upload.StorageUploadService;
import com.backend.allreva.rent.command.application.request.RentIdRequest;
import com.backend.allreva.rent.command.application.request.RentRegisterRequest;
import com.backend.allreva.rent.command.application.request.RentUpdateRequest;
import com.backend.allreva.rent.command.domain.Rent;
import com.backend.allreva.rent.command.domain.RentClosedEvent;
import com.backend.allreva.rent.command.domain.RentRepository;
import com.backend.allreva.rent.command.domain.RentSaveEvent;
import com.backend.allreva.rent.exception.RentNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RentCommandService {

    private final RentRepository rentRepository;
    private final StorageUploadService storageUploadService;

    public Long registerRent(
            final RentRegisterRequest rentRegisterRequest,
            final Long memberId) {
        Rent rent = rentRegisterRequest.toEntity(memberId);
        Rent savedRent = rentRepository.save(rent);
        Events.raise(new RentSaveEvent(savedRent));
        return savedRent.getId();
    }

    public Rent updateRent(
            final RentUpdateRequest rentUpdateRequest,
            final Long memberId) {
        Rent rent = rentRepository.findById(rentUpdateRequest.rentId())
                .orElseThrow(RentNotFoundException::new);

        rent.validateMine(memberId);

        rentRepository.deleteBoardingInfoAllByRentId(rentUpdateRequest.rentId());
        rent.updateRent(rentUpdateRequest);
        return rent;
    }

    public void closeRent(
            final RentIdRequest rentIdRequest,
            final Long memberId) {
        Rent rent = rentRepository.findById(rentIdRequest.rentId())
                .orElseThrow(RentNotFoundException::new);

        rent.validateMine(memberId);
        rent.close();
    }

    @Async
    @TransactionalEventListener
    public void closeRent(RentClosedEvent event) {
        Rent rent = rentRepository.findById(event.getRentId())
                .orElseThrow(RentNotFoundException::new);

        rent.close();
    }

    public void deleteRent(
            final RentIdRequest rentIdRequest,
            final Long memberId) {
        Rent rent = rentRepository.findById(rentIdRequest.rentId())
                .orElseThrow(RentNotFoundException::new);

        rent.validateMine(memberId);

        storageUploadService.deleteImage(rent.getDetailInfo().getImage().getUrl());
        rentRepository.delete(rent);
    }
}
