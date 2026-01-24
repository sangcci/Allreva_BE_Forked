package com.backend.allreva.module.concert.concert.application;

import com.backend.allreva.common.util.CsvUtil;
import com.backend.allreva.common.util.DateConverter;
import com.backend.allreva.module.concert.concert.application.port.ConcertDataSyncPort;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.infra.ConcertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ConcertSyncScheduler {
    private final ConcertDataSyncPort concertDataSyncPort;
    private final ConcertRepository concertRepository;

    /**
     * 공연 정보 매일 동기화
     * 매일 새벽 4시 실행
     */
    @Scheduled(cron = "0 0 4 * * *") // 매일 새벽 4시
    public void fetchDailyConcertInfoList() {
        try {
            LocalDate today = LocalDate.now();
            String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            fetchDailyConcertInfoList(formattedDate);
            log.info("{}: daily concert info update complete", formattedDate);
        } catch (Exception e) {
            log.error("Can't update daily concert info. Message: {}", e.getMessage());
        }
    }

    // 매일 업데이트 함수
    public void fetchDailyConcertInfoList(String today) {
        List<String> hallCodes = CsvUtil.readConcertHallCodes();

        LocalDate date = LocalDate.now();
        String[] dates = getStartAndEndDate(date.getYear(), date.getMonthValue());

        hallCodes.parallelStream().forEach(hallCode -> {
            List<String> concertCodes = concertDataSyncPort.fetchDailyConcertCodes(hallCode, dates[0], dates[1], today);
            concertCodes.forEach(concertCode -> {
                Concert concert = concertDataSyncPort.fetchConcertDetail(hallCode, concertCode);
                processConcertUpdateOrInsert(concert);
                log.info("All concert details updated for hall Code: {}", hallCode);
            });
        });
    }

    // 공연 정보 업데이트 혹은 새로 추가
    private void processConcertUpdateOrInsert(Concert concert) {
        String concertCode = concert.getCode().getConcertCode();
        boolean isExist = concertRepository.existsByCodeConcertCode(concertCode);

        if (isExist) {
            updateConcert(concert);
        } else {
            concertRepository.save(concert);
        }
    }

    // 기존 공연 정보 업데이트
    private void updateConcert(Concert newConcert) {
        Concert existingConcert = concertRepository.findByCodeConcertCode(newConcert.getCode().getConcertCode());
        existingConcert.updateFrom(
                newConcert.getCode(),
                newConcert.getConcertInfo(),
                newConcert.getEpisodes(),
                newConcert.getPoster(),
                newConcert.getDetailImages(),
                newConcert.getSellers()
        );
        concertRepository.save(existingConcert);
    }

    private static String[] getStartAndEndDate(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        String startDate = DateConverter.convertToyyyyMMdd(yearMonth.atDay(1));
        String endDate = DateConverter.convertToyyyyMMdd(yearMonth.atEndOfMonth());
        return new String[] { startDate, endDate };
    }
}