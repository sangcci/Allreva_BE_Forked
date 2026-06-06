package com.backend.allreva.recruitment.survey.command.application;

import com.backend.allreva.common.event.Events;
import com.backend.allreva.recruitment.survey.command.event.SurveyJoinedEvent;
import com.backend.allreva.recruitment.survey.command.event.SurveyRegisteredEvent;
import com.backend.allreva.recruitment.survey.command.implementation.SurveyJoiner;
import com.backend.allreva.recruitment.survey.command.implementation.SurveyParticipantCanceller;
import com.backend.allreva.recruitment.survey.command.implementation.SurveyParticipantReader;
import com.backend.allreva.recruitment.survey.command.implementation.SurveyReader;
import com.backend.allreva.recruitment.survey.command.implementation.SurveyRegister;
import com.backend.allreva.recruitment.survey.command.implementation.SurveyUpdater;
import com.backend.allreva.recruitment.survey.command.implementation.SurveyWriter;
import com.backend.allreva.recruitment.survey.command.input.JoinSurveyCommand;
import com.backend.allreva.recruitment.survey.command.input.OpenSurveyCommand;
import com.backend.allreva.recruitment.survey.command.input.SurveyIdCommand;
import com.backend.allreva.recruitment.survey.command.input.UpdateSurveyCommand;
import com.backend.allreva.recruitment.survey.domain.Survey;
import com.backend.allreva.recruitment.survey.domain.SurveyParticipant;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRegister surveyRegister;
    private final SurveyUpdater surveyUpdater;
    private final SurveyReader surveyReader;
    private final SurveyParticipantReader surveyParticipantReader;
    private final SurveyWriter surveyWriter;
    private final SurveyJoiner surveyJoiner;
    private final SurveyParticipantCanceller surveyParticipantCanceller;

    @Transactional
    public Long open(final OpenSurveyCommand command, final Long memberId) {
        Survey survey = surveyRegister.register(command, memberId);
        Survey savedSurvey = surveyWriter.save(survey);

        Events.raise(SurveyRegisteredEvent.from(savedSurvey));

        return savedSurvey.getId();
    }

    @Transactional
    public void update(final UpdateSurveyCommand command, final Long memberId) {
        Survey survey = surveyReader.get(command.surveyId());

        survey.validateWriter(memberId);
        surveyUpdater.update(survey, command);
        surveyWriter.save(survey);
    }

    @Transactional
    public void delete(final SurveyIdCommand command, final Long memberId) {
        Survey survey = surveyReader.get(command.surveyId());

        survey.validateWriter(memberId);
        surveyWriter.delete(survey);
    }

    @Transactional
    public Long join(final JoinSurveyCommand command, final Long memberId) {
        Survey survey = surveyReader.get(command.surveyId());

        SurveyParticipant participant = surveyJoiner.join(survey, command, memberId);
        SurveyParticipant saved = surveyWriter.save(participant);

        Events.raise(SurveyJoinedEvent.from(survey, saved));

        return saved.getId();
    }

    @Transactional
    public void cancelJoin(final Long participantId, final Long memberId) {
        SurveyParticipant participant = surveyParticipantReader.get(participantId);

        surveyParticipantCanceller.cancel(participant, memberId);
        surveyWriter.delete(participant);
    }

    @Transactional
    public void closeExpired(final LocalDate today) {
        surveyWriter.closeExpired(today);
    }
}
