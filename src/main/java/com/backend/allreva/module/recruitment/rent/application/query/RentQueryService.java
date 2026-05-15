package com.backend.allreva.module.recruitment.rent.application.query;

import com.backend.allreva.events.Events;
import com.backend.allreva.module.search.domain.event.KeywordSearchedEvent;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import com.backend.allreva.module.recruitment.rent.application.query.dto.HostedRentSummaryResponse;
import com.backend.allreva.module.recruitment.rent.application.query.dto.JoinedRentResponse;
import com.backend.allreva.module.recruitment.rent.application.query.dto.RentDetailResponse;
import com.backend.allreva.module.recruitment.rent.application.query.dto.RentParticipantResponse;
import com.backend.allreva.module.recruitment.rent.application.query.dto.RentSummaryResponse;
import com.backend.allreva.module.recruitment.rent.application.query.dto.RentThumbnail;
import com.backend.allreva.module.recruitment.rent.application.query.dto.SortType;
import com.backend.allreva.module.recruitment.rent.application.query.port.RentSearchRepository;
import com.backend.allreva.module.recruitment.rent.domain.Rent;
import com.backend.allreva.module.recruitment.rent.domain.RentRepository;
import com.backend.allreva.module.recruitment.rent.domain.participant.RentParticipant;
import com.backend.allreva.module.recruitment.rent.domain.participant.RentParticipantRepository;
import com.backend.allreva.module.recruitment.rent.exception.RentErrorCode;
import com.backend.allreva.module.search.exception.SearchErrorCode;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentQueryService {

    private final RentRepository rentRepository;
    private final RentParticipantRepository rentParticipantRepository;
    private final ConcertRepository concertRepository;
    private final ConcertHallRepository concertHallRepository;
    private final RentSearchRepository rentSearchRepository;

    public List<RentThumbnail> getRentSuggestions(final String title) {
        Events.raise(new KeywordSearchedEvent(title));
        List<RentThumbnail> thumbnails = rentSearchRepository.findThumbnailsByTitle(title, 2);
        if (thumbnails.isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return thumbnails;
    }

    public SliceResponse<RentThumbnail, Long> searchRents(final String title, final Long cursorId, final int size) {
        SliceResponse<RentThumbnail, Long> response = rentSearchRepository.findAllByTitle(title, cursorId, size);
        if (response.items().isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return response;
    }

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

    public RentDetailResponse getRentDetail(final Long id) {
        Rent rent = rentRepository.findById(id).orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));
        Concert concert = concertRepository.findById(rent.getConcertCode()).orElse(null);
        ConcertHall concertHall = concert != null
                ? concertHallRepository.findById(concert.getHallCode()).orElse(null)
                : null;
        return RentDetailResponse.from(rent, concert, concertHall);
    }

    public List<HostedRentSummaryResponse> getRentHostSummaries(
            final Long memberId, final Long lastId, final int pageSize) {
        return rentRepository.findAllByMemberId(memberId, lastId, pageSize).stream()
                .map(HostedRentSummaryResponse::from)
                .toList();
    }

    public List<RentParticipantResponse> getRentHostDetail(
            final Long memberId, final LocalDate boardingDate, final Long rentId) {
        Rent rent = rentRepository
                .findByIdAndMemberId(rentId, memberId)
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));
        rent.getBoardingSlots().stream()
                .filter(s -> s.getDate().equals(boardingDate))
                .findFirst()
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));

        return rentParticipantRepository.findAllByRentIdAndBoardingDate(rentId, boardingDate).stream()
                .map(RentParticipantResponse::from)
                .toList();
    }

    public List<JoinedRentResponse> getJoinedRentSummaries(final Long memberId, final Long lastId, final int pageSize) {
        List<RentParticipant> participants = rentParticipantRepository.findAllByMemberId(memberId, lastId, pageSize);

        return participants.stream()
                .map(participant -> {
                    Rent rent = participant.getRent();
                    return JoinedRentResponse.from(
                            participant,
                            rent,
                            rent.getBoardingSlots().stream()
                                    .filter(s -> s.getDate().equals(participant.getBoardingDate()))
                                    .findFirst()
                                    .orElse(null));
                })
                .toList();
    }

    public RentParticipantResponse getJoinedRentDetail(
            final Long memberId, final LocalDate boardingDate, final Long rentId) {
        return rentParticipantRepository
                .findByMemberIdAndBoardingDateAndRentId(memberId, boardingDate, rentId)
                .map(RentParticipantResponse::from)
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));
    }
}
