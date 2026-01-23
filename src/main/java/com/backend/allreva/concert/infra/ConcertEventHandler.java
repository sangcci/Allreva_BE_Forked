package com.backend.allreva.concert.infra;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.concert.command.domain.ViewAddedEvent;
import com.backend.allreva.concert.exception.ConcertErrorCode;
import com.backend.allreva.module.search.domain.ConcertDocument;
import com.backend.allreva.module.search.domain.ConcertSearchRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ConcertEventHandler {

    private final ConcertSearchRepository concertSearchRepository;

    @Async
    @EventListener
    public void onMessage(final ViewAddedEvent event) {
        ConcertDocument concertDocument = concertSearchRepository.findByConcertCode(event.getConcertCode())
                .orElseThrow(() -> new CustomException(ConcertErrorCode.CONCERT_NOT_FOUND));

        concertDocument.updateViewCount(event.getViewCount());
    }

}
