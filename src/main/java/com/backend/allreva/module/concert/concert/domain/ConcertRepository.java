package com.backend.allreva.module.concert.concert.domain;

import com.backend.allreva.module.concert.concert.application.dto.ConcertDateInfoResponse;
import com.backend.allreva.module.concert.concert.application.dto.ConcertDetailResponse;
import com.backend.allreva.module.concert.place.application.dto.RelatedConcertResponse;
import com.backend.allreva.module.search.application.dto.ConcertThumbnail;
import java.util.List;
import java.util.Optional;

public interface ConcertRepository {

    Concert save(Concert concert);

    Optional<Concert> findById(Long id);

    boolean existsByCodeConcertCode(String concertCode);

    Concert findByCodeConcertCode(String concertCode);

    Optional<ConcertDateInfoResponse> findStartDateAndEndDateById(Long concertId);

    ConcertDetailResponse findDetailById(Long concertId);

    List<ConcertThumbnail> getConcertMainThumbnails();

    List<RelatedConcertResponse> findRelatedConcertsByHall(
            String hallCode, Long lastId, Long lastViewCount, int pageSize);

    void deleteAll();

    void deleteAllInBatch();
}
