package com.backend.allreva.module.recruitment.rent.application;

import com.backend.allreva.common.event.Events;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.storage.upload.StorageUploadService;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.notification.domain.event.NotificationEvent;
import com.backend.allreva.module.notification.domain.value.NotificationType;
import com.backend.allreva.module.recruitment.rent.application.dto.DepositAccountResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentAdminDetailResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentAdminSummaryResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentDetailResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentIdRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinCountResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinDetailResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinIdRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinUpdateRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentRegisterRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentSummaryResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentUpdateRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.SortType;
import com.backend.allreva.module.recruitment.rent.domain.Rent;
import com.backend.allreva.module.recruitment.rent.domain.RentBoardingSlot;
import com.backend.allreva.module.recruitment.rent.domain.RentBoardingSlotRepository;
import com.backend.allreva.module.recruitment.rent.domain.RentRepository;
import com.backend.allreva.module.recruitment.rent.domain.participant.RentParticipant;
import com.backend.allreva.module.recruitment.rent.domain.participant.RentParticipantRepository;
import com.backend.allreva.module.recruitment.rent.domain.value.Bus;
import com.backend.allreva.module.recruitment.rent.domain.value.Depositor;
import com.backend.allreva.module.recruitment.rent.domain.value.Price;
import com.backend.allreva.module.recruitment.rent.domain.value.Region;
import com.backend.allreva.module.recruitment.rent.exception.RentErrorCode;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentService {

    private final RentRepository rentRepository;
    private final RentBoardingSlotRepository rentBoardingSlotRepository;
    private final RentParticipantRepository rentParticipantRepository;
    private final StorageUploadService storageUploadService;

    // -------------------------
    // Command
    // -------------------------

    @Transactional
    public Long registerRent(final RentRegisterRequest request, final Long memberId) {
        Rent rent = request.toEntity(memberId);
        Rent savedRent = rentRepository.save(rent);

        List<RentBoardingSlot> slots = request.rentBoardingDateRequests().stream()
                .map(date -> RentBoardingSlot.builder()
                        .rentId(savedRent.getId())
                        .date(date)
                        .recruitmentCount(request.recruitmentCount())
                        .build())
                .toList();
        slots.forEach(rentBoardingSlotRepository::save);

        Events.raise(NotificationEvent.builder()
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
    public void updateRent(final RentUpdateRequest request, final Long memberId) {
        Rent rent = rentRepository
                .findById(request.rentId())
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));

        rent.validateMine(memberId);

        rent.updateRent(
                request.boardingArea(),
                request.upTime(),
                request.downTime(),
                request.image(),
                request.region(),
                Bus.builder()
                        .busSize(request.busSize())
                        .busType(request.busType())
                        .maxPassenger(request.maxPassenger())
                        .build(),
                Price.builder()
                        .roundPrice(request.roundPrice())
                        .upTimePrice(request.upTimePrice())
                        .downTimePrice(request.downTimePrice())
                        .build(),
                request.endDate(),
                request.chatUrl(),
                request.refundType(),
                request.information());

        rentBoardingSlotRepository.deleteAllByRentId(request.rentId());
        List<RentBoardingSlot> newSlots = request.rentBoardingDateRequests().stream()
                .map(date -> RentBoardingSlot.builder()
                        .rentId(request.rentId())
                        .date(date)
                        .recruitmentCount(request.recruitmentCount())
                        .build())
                .toList();
        newSlots.forEach(rentBoardingSlotRepository::save);
    }

    @Transactional
    public void closeRent(final RentIdRequest request, final Long memberId) {
        Rent rent = rentRepository
                .findById(request.rentId())
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));

        rent.validateMine(memberId);
        rent.close();
    }

    @Transactional
    public void deleteRent(final RentIdRequest request, final Long memberId) {
        Rent rent = rentRepository
                .findById(request.rentId())
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));

        rent.validateMine(memberId);

        storageUploadService.deleteImage(rent.getImage().getUrl());
        rentRepository.delete(rent);
    }

    // -------------------------
    // Participant Command
    // -------------------------

    @Transactional
    public Long applyRent(final RentJoinRequest request, final Long memberId) {
        if (rentParticipantRepository.exists(memberId, request.rentId(), request.boardingDate())) {
            throw new CustomException(RentErrorCode.RENT_JOIN_ALREADY_EXISTS);
        }

        RentBoardingSlot slot = rentBoardingSlotRepository
                .findByRentIdAndDate(request.rentId(), request.boardingDate())
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));
        slot.addPassengerCount(request.passengerNum());

        RentParticipant participant = request.toEntity(memberId);
        RentParticipant saved = rentParticipantRepository.save(participant);
        return saved.getId();
    }

    @Transactional
    public void updateRentJoin(final RentJoinUpdateRequest request, final Long memberId) {
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
                request.boardingType(),
                request.refundType(),
                request.refundAccount(),
                request.boardingDate());
    }

    @Transactional
    public void cancelRentJoin(final RentJoinIdRequest request, final Long memberId) {
        RentParticipant participant = rentParticipantRepository
                .findById(request.rentParticipantId())
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_JOIN_NOT_FOUND));

        participant.validateMine(memberId);

        rentParticipantRepository.delete(participant);
    }

    // -------------------------
    // Participant Query
    // -------------------------

    @Transactional(readOnly = true)
    public List<RentJoinResponse> getRentJoinList(final Long memberId) {
        return rentParticipantRepository.findByMemberId(memberId);
    }

    // -------------------------
    // Query
    // -------------------------

    @Transactional(readOnly = true)
    public List<RentSummaryResponse> getRentMainSummaries() {
        return rentRepository.findRentSummaries(null, SortType.LATEST, null, null, 3);
    }

    @Transactional(readOnly = true)
    public List<RentSummaryResponse> getRentSummaries(
            final Region region,
            final SortType sortType,
            final LocalDate lastEndDate,
            final Long lastId,
            final int pageSize) {
        return rentRepository.findRentSummaries(region, sortType, lastEndDate, lastId, pageSize);
    }

    @Transactional(readOnly = true)
    public List<RentAdminSummaryResponse> getRentAdminSummaries(
            final Long memberId, final Long lastId, final int pageSize) {
        return rentRepository.findRentAdminSummaries(memberId, lastId, pageSize);
    }

    @Transactional(readOnly = true)
    public RentDetailResponse getRentDetail(final Long id, final Member member) {
        RentDetailResponse rentDetailResponse =
                rentRepository.findRentDetail(id).orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));
        if (member != null) {
            Set<LocalDate> appliedDates =
                    rentParticipantRepository.findAppliedBoardingDates(member.getId(), id).stream()
                            .collect(Collectors.toSet());
            rentDetailResponse
                    .getBoardingDates()
                    .forEach(response -> response.setIsApplied(appliedDates.contains(response.getDate())));
            rentDetailResponse.setRefundAccount(member.getRefundAccount());
        }
        return rentDetailResponse;
    }

    @Transactional(readOnly = true)
    public DepositAccountResponse getDepositAccount(final Long id) {
        Rent rent = rentRepository.findById(id).orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));
        return DepositAccountResponse.from(rent);
    }

    @Transactional(readOnly = true)
    public RentAdminDetailResponse getRentAdminDetail(
            final Long memberId, final LocalDate boardingDate, final Long rentId) {
        return new RentAdminDetailResponse(
                getRentAdminSummary(memberId, boardingDate, rentId),
                rentParticipantRepository
                        .findRentJoinCount(memberId, boardingDate, rentId)
                        .orElse(RentJoinCountResponse.EMPTY),
                getRentJoinDetails(boardingDate, rentId));
    }

    private RentAdminSummaryResponse getRentAdminSummary(
            final Long memberId, final LocalDate boardingDate, final Long rentId) {
        Rent rent = rentRepository
                .findByIdAndMemberId(rentId, memberId)
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));
        RentBoardingSlot slot = rentBoardingSlotRepository
                .findByRentIdAndDate(rentId, boardingDate)
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));
        return RentAdminSummaryResponse.from(rent, slot);
    }

    private List<RentJoinDetailResponse> getRentJoinDetails(final LocalDate boardingDate, final Long rentId) {
        return rentParticipantRepository.findByRentIdAndBoardingDate(rentId, boardingDate).stream()
                .map(RentJoinDetailResponse::from)
                .toList();
    }
}
