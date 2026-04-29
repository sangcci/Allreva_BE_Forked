package com.backend.allreva.module.recruitment.survey.presentation;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.auth.security.AuthMember;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.recruitment.survey.application.SurveyService;
import com.backend.allreva.module.recruitment.survey.application.dto.CreatedSurveyResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.JoinSurveyRequest;
import com.backend.allreva.module.recruitment.survey.application.dto.JoinSurveyResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.OpenSurveyRequest;
import com.backend.allreva.module.recruitment.survey.application.dto.SortType;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveyDetailResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveyIdRequest;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveySummaryResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.UpdateSurveyRequest;
import com.backend.allreva.module.recruitment.survey.domain.value.Region;
import com.backend.allreva.module.recruitment.survey.exception.SurveyErrorCode;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/surveys")
public class SurveyController implements SurveyControllerSwagger {

    private final SurveyService surveyService;

    @Override
    @PostMapping
    public Response<Long> openSurvey(
            @AuthMember Member member, @Valid @RequestBody OpenSurveyRequest openSurveyRequest) {
        return Response.onSuccess(surveyService.openSurvey(member.getId(), openSurveyRequest));
    }

    @Override
    @PatchMapping
    public Response<Void> updateSurvey(
            @AuthMember Member member, @Valid @RequestBody UpdateSurveyRequest updateSurveyRequest) {
        surveyService.updateSurvey(member.getId(), updateSurveyRequest);
        return Response.onSuccess();
    }

    @Override
    @DeleteMapping
    public Response<Void> removeSurvey(@AuthMember Member member, @RequestBody SurveyIdRequest surveyIdRequest) {
        surveyService.removeSurvey(member.getId(), surveyIdRequest);
        return Response.onSuccess();
    }

    @Override
    @GetMapping("/{surveyId}")
    public Response<SurveyDetailResponse> findSurveyDetail(@PathVariable(name = "surveyId") Long surveyId) {
        return Response.onSuccess(surveyService.findSurveyDetail(surveyId));
    }

    @Override
    @GetMapping("/list")
    public Response<List<SurveySummaryResponse>> findSurveyList(
            @RequestParam(name = "region", required = false) final Region region,
            @RequestParam(name = "sort", defaultValue = "LATEST") final SortType sortType,
            @RequestParam(name = "lastId", required = false) final Long lastId,
            @RequestParam(name = "lastEndDate", required = false) final LocalDate lastEndDate,
            @RequestParam(name = "pageSize", defaultValue = "10") final int pageSize) {
        if (lastEndDate != null && lastId == null) {
            throw new CustomException(SurveyErrorCode.SURVEY_ILLEGAL_PARAMETER);
        }
        return Response.onSuccess(surveyService.findSurveyList(region, sortType, lastId, lastEndDate, pageSize));
    }

    @Override
    @GetMapping("/main")
    public Response<List<SurveySummaryResponse>> findSurveyMainList() {
        return Response.onSuccess(surveyService.findSurveyMainList());
    }

    @Override
    @PostMapping("/apply")
    public Response<Long> joinSurvey(
            @AuthMember Member member, @Valid @RequestBody JoinSurveyRequest joinSurveyRequest) {
        return Response.onSuccess(surveyService.joinSurvey(member.getId(), joinSurveyRequest));
    }

    @Override
    @DeleteMapping("/apply/{participantId}")
    public Response<Void> cancelJoin(
            @AuthMember Member member, @PathVariable(name = "participantId") Long participantId) {
        surveyService.cancelJoin(member.getId(), participantId);
        return Response.onSuccess();
    }

    @Override
    @GetMapping("/member/list")
    public Response<List<CreatedSurveyResponse>> findCreatedSurveyList(
            @AuthMember Member member,
            @RequestParam(value = "lastSurveyId", required = false) final Long lastId,
            @RequestParam(name = "lastBoardingDate", required = false) final LocalDate lastBoardingDate,
            @RequestParam(value = "pageSize", defaultValue = "10") final int pageSize) {
        return Response.onSuccess(
                surveyService.findCreatedSurveyList(member.getId(), lastId, lastBoardingDate, pageSize));
    }

    @Override
    @GetMapping("/member/apply/list")
    public Response<List<JoinSurveyResponse>> findJoinSurveyList(
            @AuthMember Member member,
            @RequestParam(value = "lastSurveyParticipantId", required = false) final Long lastId,
            @RequestParam(value = "pageSize", defaultValue = "10") final int pageSize) {
        return Response.onSuccess(surveyService.findJoinSurveyList(member.getId(), lastId, pageSize));
    }
}
