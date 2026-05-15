package com.backend.allreva.module.recruitment.rent.application.command;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.storage.upload.StorageUploadService;
import com.backend.allreva.module.notification.domain.event.NotificationEvent;
import com.backend.allreva.module.notification.domain.value.NotificationType;
import com.backend.allreva.module.recruitment.rent.application.command.dto.RentIdRequest;
import com.backend.allreva.module.recruitment.rent.application.command.dto.RentJoinIdRequest;
import com.backend.allreva.module.recruitment.rent.application.command.dto.RentJoinRequest;
import com.backend.allreva.module.recruitment.rent.application.command.dto.RentJoinUpdateRequest;
import com.backend.allreva.module.recruitment.rent.application.command.dto.RentRegisterRequest;
import com.backend.allreva.module.recruitment.rent.application.command.dto.RentUpdateRequest;
import com.backend.allreva.module.recruitment.rent.domain.Rent;
import com.backend.allreva.module.recruitment.rent.domain.RentBoardingSlot;
import com.backend.allreva.module.recruitment.rent.domain.RentBoardingSlotRepository;
import com.backend.allreva.module.recruitment.rent.domain.RentRepository;
import com.backend.allreva.module.recruitment.rent.domain.participant.RentParticipant;
import com.backend.allreva.module.recruitment.rent.domain.participant.RentParticipantRepository;
import com.backend.allreva.module.recruitment.rent.domain.value.Bus;
import com.backend.allreva.module.recruitment.rent.domain.value.Depositor;
import com.backend.allreva.module.recruitment.rent.exception.RentErrorCode;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class RentCommandService {

    private final RentRepository rentRepository;
    private final RentBoardingSlotRepository rentBoardingSlotRepository;
    private final RentParticipantRepository rentParticipantRepository;
    private final StorageUploadService storageUploadService;

    @Transactional
    public Long registerRent(@Valid final RentRegisterRequest request, final Long memberId) {
        Rent rent = request.toEntity(memberId);
        request.rentBoardingDateRequests()
                .forEach(date -> rent.addBoardingSlot(RentBoardingSlot.builder()
                        .date(date)
                        .recruitmentCount(request.recruitmentCount())
                        .build()));
        Rent savedRent = rentRepository.save(rent);

        com.backend.allreva.common.event.Events.raise(NotificationEvent.builder()
                .type(NotificationType.RENT_REGISTERED)
                .recipientIds(List.of(memberId))
                .senderId(memberId)
                .roomId(savedRent.getId())
                .roomName(request.title())
                .content(request.title() + " 차량 대절이 등록되었습니다.")
                .build());

        return savedRent.getId();
    }

    @Transactional
    public void updateRent(@Valid final RentUpdateRequest request, final Long memberId) {
        Rent rent = rentRepository
                .findById(request.rentId())
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));

        rent.validateMine(memberId);

        rent.updateRent(
                request.image(),
                request.region(),
                request.boardingType(),
                request.upRoute(),
                request.downRoute(),
                Bus.builder()
                        .busSize(request.busSize())
                        .busType(request.busType())
                        .maxPassenger(request.maxPassenger())
                        .build(),
                request.price(),
                request.endDate(),
                request.information());

        List<RentBoardingSlot> newSlots = request.rentBoardingDateRequests().stream()
                .map(date -> RentBoardingSlot.builder()
                        .date(date)
                        .recruitmentCount(request.recruitmentCount())
                        .build())
                .toList();
        rent.replaceBoardingSlots(newSlots);
    }

    @Transactional
    public void closeRent(@Valid final RentIdRequest request, final Long memberId) {
        Rent rent = rentRepository
                .findById(request.rentId())
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));

        rent.validateMine(memberId);
        rent.close();
    }

    @Transactional
    public void deleteRent(@Valid final RentIdRequest request, final Long memberId) {
        Rent rent = rentRepository
                .findById(request.rentId())
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));

        rent.validateMine(memberId);

        storageUploadService.deleteImage(rent.getImage().getUrl());
        rentRepository.delete(rent);
    }

    @Transactional
    public Long joinRent(@Valid final RentJoinRequest request, final Long memberId) {
        if (rentParticipantRepository.exists(memberId, request.rentId(), request.boardingDate())) {
            throw new CustomException(RentErrorCode.RENT_JOIN_ALREADY_EXISTS);
        }

        int updatedRowCount = rentBoardingSlotRepository.incrementPassengerCount(
                request.rentId(), request.boardingDate(), request.passengerNum());
        if (updatedRowCount == 0) {
            throw new CustomException(RentErrorCode.SLOT_FULL);
        }

        Rent rent = rentRepository
                .findById(request.rentId())
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));
        RentParticipant participant = request.toEntity(rent, memberId);
        RentParticipant saved = rentParticipantRepository.save(participant);
        return saved.getId();
    }

    @Transactional
    public void updateRentJoin(@Valid final RentJoinUpdateRequest request, final Long memberId) {
        RentParticipant participant = rentParticipantRepository
                .findById(request.rentParticipantId())
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_JOIN_NOT_FOUND));

        participant.validateMine(memberId);

        participant.update(
                Depositor.builder()
                        .depositorName(request.depositorName())
                        .depositorTime(request.depositorTime())
                        .phone(request.phone())
                        .build(),
                request.passengerNum(),
                request.refundType(),
                request.refundAccount(),
                request.boardingDate());
    }

    @Transactional
    public void cancelRentJoin(@Valid final RentJoinIdRequest request, final Long memberId) {
        RentParticipant participant = rentParticipantRepository
                .findById(request.rentParticipantId())
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_JOIN_NOT_FOUND));

        participant.validateMine(memberId);

        int updated = rentBoardingSlotRepository.decrementPassengerCount(
                participant.getRent().getId(), participant.getBoardingDate(), participant.getPassengerNum());
        if (updated == 0) {
            log.warn(
                    "passengerCount decrement skipped: rentId={}, boardingDate={}, passengerNum={}",
                    participant.getRent().getId(),
                    participant.getBoardingDate(),
                    participant.getPassengerNum());
        }

        rentParticipantRepository.delete(participant);
    }
}
