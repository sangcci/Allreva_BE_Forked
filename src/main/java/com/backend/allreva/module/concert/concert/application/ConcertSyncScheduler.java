package com.backend.allreva.module.concert.concert.application;

import com.backend.allreva.module.concert.concert.application.dto.ConcertSummary;
import com.backend.allreva.module.concert.concert.application.port.ConcertDataSyncPort;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.concert.domain.value.ConcertStatus;
import com.backend.allreva.module.concert.concert.infra.kopis.DateConverter;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class ConcertSyncScheduler {

    private static final int KOPIS_RATE_LIMIT_MILLIS = 100;

    private final ConcertDataSyncPort concertDataSyncPort;
    private final ConcertHallRepository concertHallRepository;
    private final ConcertRepository concertRepository;

    /** 공연 정보 매일 동기화 — 매일 새벽 4시 */
    @Scheduled(cron = "0 0 4 * * *")
    public void fetchDailyScheduled() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        try {
            fetchDailyConcertInfoList(today);
            log.info("{}: daily concert info update complete", today);
        } catch (Exception e) {
            log.error("Can't update daily concert info. Message: {}", e.getMessage());
        }
    }

    public void fetchDailyConcertInfoList(final String today) {
        List<String> hallIds = concertHallRepository.findAllIds();
        Map<String, ConcertStatus> statusMap = concertRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Concert::getConcertCode, c -> c.getConcertInfo().getPerformStatus()));

        String[] dates =
                getStartAndEndDate(LocalDate.now().getYear(), LocalDate.now().getMonthValue());

        for (String hallId : hallIds) {
            try {
                List<ConcertSummary> summaries =
                        concertDataSyncPort.fetchDailyConcertSummaries(hallId, dates[0], dates[1], today);

                for (ConcertSummary summary : summaries) {
                    ConcertStatus existing = statusMap.get(summary.concertCode());

                    // Skip if existing concert is COMPLETED
                    if (existing == ConcertStatus.COMPLETED) {
                        continue;
                    }
                    // Skip if status unchanged
                    if (existing != null && existing == summary.status()) {
                        continue;
                    }

                    Concert fetched = concertDataSyncPort.fetchConcertDetail(hallId, summary.concertCode());
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
                log.debug("Concert sync complete for hall: {}", hallId);
                Thread.sleep(KOPIS_RATE_LIMIT_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Concert sync interrupted: {}", hallId);
                break;
            } catch (Exception e) {
                log.error("Concert sync failed for hall {}: {}", hallId, e.getMessage());
            }
        }
    }

    private static String[] getStartAndEndDate(final int year, final int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        String startDate = DateConverter.convertToyyyyMMdd(yearMonth.atDay(1));
        String endDate = DateConverter.convertToyyyyMMdd(yearMonth.atEndOfMonth());
        return new String[] {startDate, endDate};
    }
}
