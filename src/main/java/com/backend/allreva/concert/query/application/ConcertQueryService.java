package com.backend.allreva.concert.query.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.allreva.concert.command.domain.ConcertRepository;
import com.backend.allreva.concert.query.application.response.ConcertDetailResponse;
import com.backend.allreva.module.search.application.ConcertSearchService;
import com.backend.allreva.module.search.application.dto.ConcertMainResponse;
import com.backend.allreva.module.search.application.dto.ConcertThumbnail;
import com.backend.allreva.module.search.domain.SortDirection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class ConcertQueryService {

    private final ConcertRepository concertRepository;
    private final ConcertSearchService concertSearchService;

    public ConcertDetailResponse findDetailById(final Long concertId) {
        concertRepository.increaseViewCount(concertId);
        return concertRepository.findDetailById(concertId);
    }

    public ConcertMainResponse getConcertMain(
            final String address,
            final List<Object> searchAfter,
            final int size,
            final SortDirection sortDirection) {
        return concertSearchService.searchMainConcerts(address, searchAfter, size, sortDirection);
    }

    public List<ConcertThumbnail> getConcertMainList() {
        return concertRepository.getConcertMainThumbnails();
    }

}
