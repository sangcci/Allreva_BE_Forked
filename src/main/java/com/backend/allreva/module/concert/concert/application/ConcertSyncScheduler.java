package com.backend.allreva.module.concert.concert.application;

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
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Component
public class ConcertSyncScheduler {

    private static final int KOPIS_RATE_LIMIT_MILLIS = 100;

    private final ConcertDataSyncPort concertDataSyncPort;
    private final ConcertRepository concertRepository;
    private final ConcertHallRepository concertHallRepository;

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
        String[] dates =
                getStartAndEndDate(LocalDate.now().getYear(), LocalDate.now().getMonthValue());

        for (String hallId : hallIds) {
            try {
                List<String> concertCodes =
                        concertDataSyncPort.fetchDailyConcertCodes(hallId, dates[0], dates[1], today);
                for (String concertCode : concertCodes) {
                    Concert fetched = concertDataSyncPort.fetchConcertDetail(hallId, concertCode);
                    processConcertUpsert(fetched);
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

    private static String[] getStartAndEndDate(final int year, final int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        String startDate = DateConverter.convertToyyyyMMdd(yearMonth.atDay(1));
        String endDate = DateConverter.convertToyyyyMMdd(yearMonth.atEndOfMonth());
        return new String[] {startDate, endDate};
    }
}
