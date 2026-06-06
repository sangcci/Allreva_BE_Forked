package com.backend.allreva.recruitment.rent.query.application;

import com.backend.allreva.common.event.Events;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.recruitment.rent.domain.RentErrorCode;
import com.backend.allreva.recruitment.rent.domain.SortType;
import com.backend.allreva.recruitment.rent.query.implementation.RentFinderPort;
import com.backend.allreva.recruitment.rent.query.model.HostedRentSummaryResult;
import com.backend.allreva.recruitment.rent.query.model.JoinedRentResult;
import com.backend.allreva.recruitment.rent.query.model.RentDetailResult;
import com.backend.allreva.recruitment.rent.query.model.RentParticipantResult;
import com.backend.allreva.recruitment.rent.query.model.RentSummaryResult;
import com.backend.allreva.recruitment.rent.query.model.RentThumbnail;
import com.backend.allreva.recruitment.rent.query.model.RentThumbnailResult;
import com.backend.allreva.search.domain.KeywordSearchedEvent;
import com.backend.allreva.search.domain.SearchErrorCode;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentFinder {

    private final RentFinderPort rentFinder;

    public List<RentThumbnailResult> getRentSuggestions(final String title) {
        Events.raise(new KeywordSearchedEvent(title));
        List<RentThumbnailResult> thumbnails = rentFinder.findThumbnailsByTitle(title, 2).stream()
                .map(RentThumbnailResult::from)
                .toList();
        if (thumbnails.isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return thumbnails;
    }

    public SliceResponse<RentThumbnailResult, Long> searchRents(
            final String title, final Long cursorId, final int size) {
        SliceResponse<RentThumbnail, Long> response = rentFinder.findAllByTitle(title, cursorId, size);
        if (response.items().isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return new SliceResponse<>(
                response.items().stream().map(RentThumbnailResult::from).toList(), response.nextCursor());
    }

    public List<RentSummaryResult> getRentSummaries(
            final String region,
            final SortType sortType,
            final LocalDate lastEndDate,
            final Long lastId,
            final int pageSize) {
        return rentFinder.findRentSummaries(region, sortType, lastEndDate, lastId, pageSize).stream()
                .map(RentSummaryResult::from)
                .toList();
    }

    public RentDetailResult getRentDetail(final Long id) {
        return rentFinder
                .findRentDetail(id)
                .map(RentDetailResult::from)
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));
    }

    public List<HostedRentSummaryResult> getRentHostSummaries(
            final Long memberId, final Long lastId, final int pageSize) {
        return rentFinder.findHostedRentSummaries(memberId, lastId, pageSize).stream()
                .map(HostedRentSummaryResult::from)
                .toList();
    }

    public List<RentParticipantResult> getRentHostDetail(
            final Long memberId, final LocalDate boardingDate, final Long rentId) {
        return rentFinder
                .findHostedRentParticipants(memberId, rentId, boardingDate)
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND))
                .stream()
                .map(RentParticipantResult::from)
                .toList();
    }

    public List<JoinedRentResult> getJoinedRentSummaries(final Long memberId, final Long lastId, final int pageSize) {
        return rentFinder.findJoinedRents(memberId, lastId, pageSize).stream()
                .map(JoinedRentResult::from)
                .toList();
    }

    public RentParticipantResult getJoinedRentDetail(
            final Long memberId, final LocalDate boardingDate, final Long rentId) {
        return rentFinder
                .findJoinedRentParticipant(memberId, rentId, boardingDate)
                .map(RentParticipantResult::from)
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_NOT_FOUND));
    }
}
