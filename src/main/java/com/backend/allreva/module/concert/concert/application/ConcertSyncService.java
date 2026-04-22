package com.backend.allreva.module.concert.concert.application;

import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.concert.domain.value.ConcertStatus;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Component
public class ConcertSyncService {

    private final ConcertRepository concertRepository;

    @Transactional
    public void processConcertUpsert(final Concert fetched) {
        String concertCode = fetched.getCode().getConcertCode();
        Optional<Concert> existing = concertRepository.findByCodeConcertCode(concertCode);

        if (existing.isEmpty()) {
            concertRepository.save(fetched);
            return;
        }

        Concert existingConcert = existing.get();
        if (existingConcert.getConcertInfo().getPerformStatus() == ConcertStatus.COMPLETED) {
            log.debug("Skipping COMPLETED concert: {}", concertCode);
            return;
        }

        existingConcert.updateFrom(
                fetched.getCode(),
                fetched.getConcertInfo(),
                fetched.getEpisodes(),
                fetched.getPoster(),
                fetched.getDetailImages(),
                fetched.getSellers());
        concertRepository.save(existingConcert);
    }
}
