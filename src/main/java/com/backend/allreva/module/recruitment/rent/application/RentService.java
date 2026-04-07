package com.backend.allreva.module.recruitment.rent.application;

import com.backend.allreva.common.event.Events;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.storage.upload.StorageUploadService;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.notification.domain.event.NotificationEvent;
import com.backend.allreva.module.notification.domain.value.NotificationType;
import com.backend.allreva.module.recruitment.rent.application.dto.HostedRentDetailResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.HostedRentSummaryResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.JoinedRentDetailResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.JoinedRentResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentDetailResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentIdRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinIdRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinUpdateRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentMeResponse;
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
import com.backend.allreva.module.recruitment.rent.exception.RentErrorCode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private final ConcertRepository concertRepository;
    private final ConcertHallRepository concertHallRepository;
    private final StorageUploadService storageUploadService;

    // Anonymous
    @Transactional(readOnly = true)
    public List<RentSummaryResponse> getRentSummaries(
            final String region,
            final SortType sortType,
            final LocalDate lastEndDate,
            final Long lastId,
            final int pageSize) {
        return rentRepository.findAll(region, sortType, lastEndDate, lastId, pageSize).stream()
                .map(RentSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RentDetailResponse getRentDetail(final Long id) {
        Rent rent = rentRepository.findById(id).orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));
        Concert concert = concertRepository.findById(rent.getConcertId()).orElse(null);
        ConcertHall concertHall = concert != null
                ? concertHallRepository
                        .findById(concert.getCode().getHallCode())
                        .orElse(null)
                : null;
        return RentDetailResponse.from(rent, concert, concertHall);
    }

    // User
    @Transactional(readOnly = true)
    public RentMeResponse getRentDetailMe(final Long id, final Member member) {
        Set<LocalDate> appliedDates =
                new HashSet<>(rentParticipantRepository.findAppliedBoardingDates(member.getId(), id));
        return new RentMeResponse(appliedDates, member.getRefundAccount());
    }

    // Host
    @Transactional
    public Long registerRent(final RentRegisterRequest request, final Long memberId) {
        Rent rent = request.toEntity(memberId);
        request.rentBoardingDateRequests()
                .forEach(date -> rent.addBoardingSlot(RentBoardingSlot.builder()
                        .date(date)
                        .recruitmentCount(request.recruitmentCount())
                        .build()));
        Rent savedRent = rentRepository.save(rent);

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

    @Transactional(readOnly = true)
    public List<HostedRentSummaryResponse> getRentHostSummaries(
            final Long memberId, final Long lastId, final int pageSize) {
        return rentRepository.findAllByMemberId(memberId, lastId, pageSize).stream()
                .map(HostedRentSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public HostedRentDetailResponse getRentHostDetail(
            final Long memberId, final LocalDate boardingDate, final Long rentId) {
        // valid not found
        Rent rent = rentRepository
                .findByIdAndMemberId(rentId, memberId)
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));
        rent.getBoardingSlots().stream()
                .filter(s -> s.getDate().equals(boardingDate))
                .findFirst()
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));

        // find data
        List<JoinedRentDetailResponse> joinDetails =
                rentParticipantRepository.findAllByRentIdAndBoardingDate(rentId, boardingDate).stream()
                        .map(JoinedRentDetailResponse::from)
                        .toList();

        // mapping
        return new HostedRentDetailResponse(HostedRentSummaryResponse.from(rent), joinDetails);
    }

    // Participant
    @Transactional
    public Long joinRent(final RentJoinRequest request, final Long memberId) {
        if (rentParticipantRepository.exists(memberId, request.rentId(), request.boardingDate())) {
            throw new CustomException(RentErrorCode.RENT_JOIN_ALREADY_EXISTS);
        }

        int updatedRowCount = rentBoardingSlotRepository.incrementPassengerCount(
                request.rentId(), request.boardingDate(), request.passengerNum());
        if (updatedRowCount == 0) {
            throw new CustomException(RentErrorCode.SLOT_FULL);
        }

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

    @Transactional(readOnly = true)
    public List<JoinedRentResponse> getJoinedRentSummaries(final Long memberId) {
        List<RentParticipant> participants = rentParticipantRepository.findAllByMemberId(memberId);

        List<Long> rentIds =
                participants.stream().map(RentParticipant::getRentId).distinct().toList();
        Map<Long, Rent> rentsById =
                rentRepository.findAllByIds(rentIds).stream().collect(Collectors.toMap(Rent::getId, r -> r));

        return participants.stream()
                .map(participant -> {
                    Rent rent = rentsById.get(participant.getRentId());
                    if (rent == null) return null;
                    RentBoardingSlot slot = rent.getBoardingSlots().stream()
                            .filter(s -> s.getDate().equals(participant.getBoardingDate()))
                            .findFirst()
                            .orElse(null);
                    return JoinedRentResponse.from(participant, rent, slot);
                })
                .filter(response -> response != null)
                .toList();
    }

    @Transactional(readOnly = true)
    public JoinedRentDetailResponse getJoinedRentDetail(
            final Long memberId, final LocalDate boardingDate, final Long rentId) {
        return rentParticipantRepository
                .findByMemberIdAndBoardingDateAndRentId(memberId, boardingDate, rentId)
                .map(JoinedRentDetailResponse::from)
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));
    }
}
