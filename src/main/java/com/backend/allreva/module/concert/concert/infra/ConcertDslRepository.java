package com.backend.allreva.module.concert.concert.infra;

import com.backend.allreva.module.concert.concert.application.dto.ConcertDetailResponse;
import com.backend.allreva.module.search.application.dto.ConcertThumbnail;
import com.backend.allreva.module.concert.hall.application.dto.RelatedConcertResponse;

import java.util.List;

public interface ConcertDslRepository {
    ConcertDetailResponse findDetailById(Long concertId);

    List<ConcertThumbnail> getConcertMainThumbnails();

    List<RelatedConcertResponse> findRelatedConcertsByHall(String hallCode, Long lastId, Long lastViewCount, int pageSize);
}
