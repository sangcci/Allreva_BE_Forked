package com.backend.allreva.concert.concert.command.implementation;

import com.backend.allreva.concert.concert.domain.Concert;
import com.backend.allreva.concert.concert.domain.ConcertRepository;
import com.backend.allreva.concert.concert.domain.ConcertStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcertWriter {

    private final ConcertRepository concertRepository;

    public Concert save(final Concert concert) {
        return concertRepository.save(concert);
    }

    public boolean isAlreadyCompleted(final String concertCode) {
        return concertRepository
                .findById(concertCode)
                .map(concert -> concert.getConcertInfo().getPerformStatus() == ConcertStatus.COMPLETED)
                .orElse(false);
    }

    public Concert upsert(final Concert fetched) {
        return concertRepository
                .findById(fetched.getConcertCode())
                .map(existing -> update(existing, fetched))
                .orElseGet(() -> save(fetched));
    }

    private Concert update(final Concert existing, final Concert fetched) {
        existing.updateFrom(fetched);
        return save(existing);
    }
}
