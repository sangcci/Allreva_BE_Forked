package com.backend.allreva.recruitment.rent.query.implementation;

import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.recruitment.rent.domain.SortType;
import com.backend.allreva.recruitment.rent.query.model.HostedRentSummary;
import com.backend.allreva.recruitment.rent.query.model.JoinedRent;
import com.backend.allreva.recruitment.rent.query.model.RentDetail;
import com.backend.allreva.recruitment.rent.query.model.RentParticipantItem;
import com.backend.allreva.recruitment.rent.query.model.RentSummary;
import com.backend.allreva.recruitment.rent.query.model.RentThumbnail;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RentFinderPort {

    List<RentThumbnail> findThumbnailsByTitle(String title, int limit);

    SliceResponse<RentThumbnail, Long> findAllByTitle(String query, Long cursorId, int pageSize);

    List<RentSummary> findRentSummaries(
            String region, SortType sortType, LocalDate lastEndDate, Long lastId, int pageSize);

    Optional<RentDetail> findRentDetail(Long rentId);

    List<HostedRentSummary> findHostedRentSummaries(Long memberId, Long lastId, int pageSize);

    Optional<List<RentParticipantItem>> findHostedRentParticipants(Long memberId, Long rentId, LocalDate boardingDate);

    List<JoinedRent> findJoinedRents(Long memberId, Long lastId, int pageSize);

    Optional<RentParticipantItem> findJoinedRentParticipant(Long memberId, Long rentId, LocalDate boardingDate);
}
