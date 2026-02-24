package com.backend.allreva.module.recruitment.survey.application;

import com.backend.allreva.common.event.Events;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.concert.concert.application.dto.ConcertDateInfoResponse;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.concert.exception.ConcertErrorCode;
import com.backend.allreva.module.notification.domain.event.NotificationEvent;
import com.backend.allreva.module.notification.domain.value.NotificationType;
import com.backend.allreva.module.recruitment.survey.application.dto.CreatedSurveyResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.JoinSurveyRequest;
import com.backend.allreva.module.recruitment.survey.application.dto.JoinSurveyResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.OpenSurveyRequest;
import com.backend.allreva.module.recruitment.survey.application.dto.SortType;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveyDetailResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveyIdRequest;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveyMainResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveySummaryResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.UpdateSurveyRequest;
import com.backend.allreva.module.recruitment.survey.domain.Survey;
import com.backend.allreva.module.recruitment.survey.domain.SurveyRepository;
import com.backend.allreva.module.recruitment.survey.domain.participant.SurveyParticipant;
import com.backend.allreva.module.recruitment.survey.domain.participant.SurveyParticipantRepository;
import com.backend.allreva.module.recruitment.survey.domain.value.Region;
import com.backend.allreva.module.recruitment.survey.exception.SurveyErrorCode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final SurveyParticipantRepository surveyParticipantRepository;
    private final ConcertRepository concertRepository;

    /**
     * 수요조사 개설
     */
    public Long openSurvey(final Long memberId, final OpenSurveyRequest request) {
        validateBoardingDates(request.concertId(), request.boardingDates());

        Survey survey = surveyRepository.save(Survey.builder()
                .memberId(memberId)
                .concertId(request.concertId())
                .title(request.title())
                .endDate(request.endDate())
                .information(request.information())
                .artistName(request.artistName())
                .region(request.region())
                .maxPassenger(request.maxPassenger())
                .boardingDates(request.boardingDates())
                .build());

        Events.raise(NotificationEvent.builder()
                .type(NotificationType.SURVEY_REGISTERED)
                .recipientIds(List.of(memberId))
                .senderId(memberId)
                .roomId(survey.getId())
                .roomName(request.title())
                .content(request.title() + " 수요조사가 등록되었습니다.")
                .build());

        return survey.getId();
    }

    /**
     * 수요조사 수정
     */
    public void updateSurvey(final Long memberId, final UpdateSurveyRequest request) {
        Survey survey = findSurvey(request.surveyId());

        validateBoardingDates(survey.getConcertId(), request.boardingDates());

        survey.isWriter(memberId);
        survey.update(
                request.title(),
                request.region(),
                request.endDate(),
                request.maxPassenger(),
                request.information(),
                request.boardingDates());
    }

    /**
     * 수요조사 삭제
     */
    public void removeSurvey(final Long memberId, final SurveyIdRequest surveyIdRequest) {
        Survey survey = findSurvey(surveyIdRequest.surveyId());
        survey.isWriter(memberId);
        surveyRepository.delete(survey);
    }

    /**
     * 수요조사 참여 (응답 제출)
     */
    public Long joinSurvey(final Long memberId, final JoinSurveyRequest request) {
        Survey survey = findSurvey(request.surveyId());
        survey.containsBoardingDate(request.boardingDate());

        SurveyParticipant participant = SurveyParticipant.builder()
                .memberId(memberId)
                .surveyId(request.surveyId())
                .boardingDate(request.boardingDate())
                .boardingType(request.boardingType())
                .passengerNum(request.passengerNum())
                .notified(request.notified())
                .build();

        return surveyParticipantRepository.save(participant).getId();
    }

    /**
     * 수요조사 참여 취소
     */
    public void cancelJoin(final Long memberId, final Long participantId) {
        SurveyParticipant participant = surveyParticipantRepository.findById(participantId)
                .orElseThrow(() -> new CustomException(SurveyErrorCode.SURVEY_PARTICIPANT_NOT_FOUND));
        surveyParticipantRepository.delete(participant);
    }

    /**
     * 내가 개설한 수요조사 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CreatedSurveyResponse> findCreatedSurveyList(
            final Long memberId,
            final Long lastId,
            final LocalDate lastBoardingDate,
            final int pageSize) {
        return surveyParticipantRepository.findCreatedSurveyList(memberId, lastId, lastBoardingDate, pageSize);
    }

    /**
     * 내가 참여한 수요조사 목록 조회
     */
    @Transactional(readOnly = true)
    public List<JoinSurveyResponse> findJoinSurveyList(
            final Long memberId,
            final Long lastId,
            final int pageSize) {
        return surveyParticipantRepository.findJoinSurveyList(memberId, lastId, pageSize);
    }

    /**
     * 수요조사 상세 조회
     */
    @Transactional(readOnly = true)
    public SurveyDetailResponse findSurveyDetail(final Long surveyId) {
        return surveyRepository.findSurveyDetail(surveyId);
    }

    /**
     * 수요조사 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SurveySummaryResponse> findSurveyList(
            final Region region,
            final SortType sortType,
            final Long lastId,
            final LocalDate lastEndDate,
            final int pageSize) {
        return surveyRepository.findSurveyList(region, sortType, lastId, lastEndDate, pageSize);
    }

    @Transactional(readOnly = true)
    public Optional<SurveyMainResponse> findSurveyWithParticipationCount(final Long surveyId) {
        return surveyRepository.findSurveyWithParticipationCount(surveyId);
    }

    @Transactional(readOnly = true)
    public List<SurveySummaryResponse> findSurveyMainList() {
        return surveyRepository.findSurveyMainList();
    }

    private void validateBoardingDates(final Long concertId, final List<LocalDate> boardingDates) {
        ConcertDateInfoResponse dateInfo = findStartDateAndEndDateById(concertId);

        LocalDate concertStartDate = dateInfo.getStartDate();
        LocalDate concertEndDate = dateInfo.getEndDate();

        for (LocalDate boardingDate : boardingDates) {
            if (boardingDate.isBefore(concertStartDate) || boardingDate.isAfter(concertEndDate)) {
                throw new CustomException(SurveyErrorCode.SURVEY_INVALID_BOARDING_DATE);
            }
        }
    }

    private ConcertDateInfoResponse findStartDateAndEndDateById(final Long concertId) {
        return concertRepository.findStartDateAndEndDateById(concertId)
                .orElseThrow(() -> new CustomException(ConcertErrorCode.CONCERT_NOT_FOUND));
    }

    private Survey findSurvey(final Long surveyId) {
        return surveyRepository.findById(surveyId)
                .orElseThrow(() -> new CustomException(SurveyErrorCode.SURVEY_NOT_FOUND));
    }
}
