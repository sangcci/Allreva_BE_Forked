package com.backend.allreva.concert.concert.query.implementation;

import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.concert.concert.domain.SortDirection;
import com.backend.allreva.concert.concert.query.model.ConcertDetail;
import com.backend.allreva.concert.concert.query.model.ConcertThumbnail;
import com.backend.allreva.concert.concert.query.model.RelatedConcert;
import java.util.List;
import java.util.Optional;

public interface ConcertFinderPort {

    List<ConcertThumbnail> findThumbnailsByTitle(String title, int limit);

    SliceResponse<ConcertThumbnail, String> findAllByTitle(String query, String cursorCode, int pageSize);

    SliceResponse<ConcertThumbnail, String> findAllByAddressAndSortDirection(
            String address, String cursorCode, int pageSize, SortDirection sortDirection);

    Optional<ConcertDetail> findConcertDetail(String concertCode);

    List<RelatedConcert> findRelatedConcerts(String hallCode, String lastConcertCode, int pageSize);
}
