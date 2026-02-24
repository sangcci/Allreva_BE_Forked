package com.backend.allreva.module.recruitment.rent.application;

import com.backend.allreva.common.event.Events;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.storage.upload.StorageUploadService;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.notification.domain.event.NotificationEvent;
import com.backend.allreva.module.notification.domain.value.NotificationType;
import com.backend.allreva.module.recruitment.chat.application.GroupChatService;
import com.backend.allreva.module.recruitment.chat.application.dto.AddGroupChatRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.DepositAccountResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentAdminDetailResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentAdminSummaryResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentDetailResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentIdRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentRegisterRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentSummaryResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentUpdateRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.SortType;
import com.backend.allreva.module.recruitment.rent.domain.Rent;
import com.backend.allreva.module.recruitment.rent.domain.RentBoardingInfo;
import com.backend.allreva.module.recruitment.rent.domain.RentRepository;
import com.backend.allreva.module.recruitment.rent.domain.event.RentClosedEvent;
import com.backend.allreva.module.recruitment.rent.domain.value.Bus;
import com.backend.allreva.module.recruitment.rent.domain.value.Price;
import com.backend.allreva.module.recruitment.rent.domain.value.Region;
import com.backend.allreva.module.recruitment.rent.exception.RentErrorCode;
import com.backend.allreva.rent_join.command.domain.RentJoinRepository;
import com.backend.allreva.rent_join.query.response.RentJoinCountResponse;
import com.backend.allreva.rent_join.query.response.RentJoinDetailResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RentService {

    private final RentRepository rentRepository;
    private final RentJoinRepository rentJoinRepository;
    private final StorageUploadService storageUploadService;
    private final GroupChatService groupChatService;

    // -------------------------
    // Command
    // -------------------------

    public Long registerRent(
            final RentRegisterRequest request,
            final Long memberId) {
        Rent rent = request.toEntity(memberId);
        Rent savedRent = rentRepository.save(rent);

        groupChatService.add(
                new AddGroupChatRequest(
                        request.title(),
                        request.maxPassenger()),
                request.image(),
                memberId);

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

    public void updateRent(
            final RentUpdateRequest request,
            final Long memberId) {
        Rent rent = rentRepository.findById(request.rentId())
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));

        rent.validateMine(memberId);

        rentRepository.deleteBoardingInfoAllByRentId(request.rentId());

        List<RentBoardingInfo> newBoardingInfos = request.rentBoardingDateRequests().stream()
                .map(date -> RentBoardingInfo.builder()
                        .rent(rent)
                        .date(date)
                        .recruitmentCount(request.recruitmentCount())
                        .build())
                .toList();

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
                request.information(),
                newBoardingInfos);
    }

    public void closeRent(
            final RentIdRequest request,
            final Long memberId) {
        Rent rent = rentRepository.findById(request.rentId())
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));

        rent.validateMine(memberId);
        rent.close();
    }

    @Async
    @TransactionalEventListener
    public void closeRent(RentClosedEvent event) {
        Rent rent = rentRepository.findById(event.getRentId())
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));

        rent.close();
    }

    public void deleteRent(
            final RentIdRequest request,
            final Long memberId) {
        Rent rent = rentRepository.findById(request.rentId())
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));

        rent.validateMine(memberId);

        storageUploadService.deleteImage(rent.getImage().getUrl());
        rentRepository.delete(rent);
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
            final Long memberId,
            final Long lastId,
            final int pageSize) {
        return rentRepository.findRentAdminSummaries(memberId, lastId, pageSize);
    }

    @Transactional(readOnly = true)
    public RentDetailResponse getRentDetail(final Long id, final Member member) {
        RentDetailResponse rentDetailResponse = rentRepository.findRentDetail(id)
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));
        if (member != null) {
            rentDetailResponse.getBoardingDates().forEach(response -> response.setIsApplied(
                    rentJoinRepository.exists(member.getId(), id, response.getDate())));
            rentDetailResponse.setRefundAccount(member.getRefundAccount());
        }
        return rentDetailResponse;
    }

    @Transactional(readOnly = true)
    public DepositAccountResponse getDepositAccount(final Long id) {
        Rent rent = rentRepository.findById(id)
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));
        return DepositAccountResponse.from(rent);
    }

    @Transactional(readOnly = true)
    public RentAdminDetailResponse getRentAdminDetail(
            final Long memberId,
            final LocalDate boardingDate,
            final Long rentId) {
        return new RentAdminDetailResponse(
                getRentAdminSummary(memberId, boardingDate, rentId),
                rentRepository.findRentJoinCount(memberId, boardingDate, rentId)
                        .orElse(RentJoinCountResponse.EMPTY),
                getRentJoinDetails(boardingDate, rentId));
    }

    private RentAdminSummaryResponse getRentAdminSummary(
            final Long memberId,
            final LocalDate boardingDate,
            final Long rentId) {
        return rentRepository.findByIdAndMemberId(rentId, memberId)
                .flatMap(rent -> rent.getBoardingInfos().stream()
                        .filter(info -> info.getDate().equals(boardingDate))
                        .map(info -> RentAdminSummaryResponse.from(rent, info))
                        .findFirst())
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));
    }

    private List<RentJoinDetailResponse> getRentJoinDetails(
            final LocalDate boardingDate,
            final Long rentId) {
        return rentJoinRepository.findByRentIdAndBoardingDate(rentId, boardingDate).stream()
                .map(RentJoinDetailResponse::from)
                .toList();
    }
}
