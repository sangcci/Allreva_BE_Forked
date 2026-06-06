package com.backend.allreva.recruitment.survey.command.implementation;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.concert.concert.domain.Concert;
import com.backend.allreva.concert.concert.domain.ConcertErrorCode;
import com.backend.allreva.concert.concert.domain.ConcertRepository;
import com.backend.allreva.recruitment.survey.domain.SurveyErrorCode;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SurveyBoardingDateChecker {

    private final ConcertRepository concertRepository;

    public void check(final String concertCode, final List<LocalDate> boardingDates) {
        Concert concert = concertRepository
                .findById(concertCode)
                .orElseThrow(() -> new CustomException(ConcertErrorCode.CONCERT_NOT_FOUND));

        boardingDates.forEach(date -> {
            if (!concert.isValidBoardingDate(date)) {
                throw new CustomException(SurveyErrorCode.SURVEY_INVALID_BOARDING_DATE);
            }
        });
    }
}
