package com.backend.allreva.module.concert.concert.application;

import com.backend.allreva.module.concert.concert.application.dto.ConcertSummary;
import com.backend.allreva.module.concert.concert.application.port.ConcertDataSyncPort;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.concert.domain.value.ConcertStatus;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class ConcertSyncService {

    private static final int KOPIS_RATE_LIMIT_MILLIS = 100;

    private final ConcertDataSyncPort concertDataSyncPort;
    private final ConcertHallRepository concertHallRepository;
    private final ConcertRepository concertRepository;

    @CacheEvict(
            cacheNames = {"concertMain", "concertSearch", "concertRelated"},
            allEntries = true)
    public void fetchDailyConcertInfoList(final LocalDate today) {
        List<String> hallCodes = concertHallRepository.findAllHallCodes();
        Map<String, ConcertStatus> statusMap = concertRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Concert::getConcertCode, c -> c.getConcertInfo().getPerformStatus()));

        YearMonth yearMonth = YearMonth.from(today);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        for (String hallCode : hallCodes) {
            try {
                List<ConcertSummary> summaries =
                        concertDataSyncPort.fetchDailyConcertSummaries(hallCode, startDate, endDate, today);

                for (ConcertSummary summary : summaries) {
                    ConcertStatus existing = statusMap.get(summary.concertCode());

                    if (existing == ConcertStatus.COMPLETED) {
                        continue;
                    }
                    if (existing != null && existing == summary.status()) {
                        continue;
                    }

                    Concert fetched = concertDataSyncPort.fetchConcertDetail(hallCode, summary.concertCode());
                    concertRepository
                            .findById(summary.concertCode())
                            .ifPresentOrElse(
                                    c -> {
                                        c.updateFrom(fetched);
                                        concertRepository.save(c);
                                    },
                                    () -> concertRepository.save(fetched));

                    Thread.sleep(KOPIS_RATE_LIMIT_MILLIS);
                }
                log.debug("Concert sync complete for hall: {}", hallCode);
                Thread.sleep(KOPIS_RATE_LIMIT_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Concert sync interrupted: {}", hallCode);
                break;
            } catch (Exception e) {
                log.error("Concert sync failed for hall {}: {}", hallCode, e.getMessage());
            }
        }
    }
}
