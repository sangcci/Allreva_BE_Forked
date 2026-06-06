package com.backend.allreva.recruitment.survey;

import com.backend.allreva.auth.security.AuthMember;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.common.web.response.View;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.recruitment.survey.domain.Region;
import com.backend.allreva.recruitment.survey.domain.SortType;
import com.backend.allreva.recruitment.survey.domain.SurveyErrorCode;
import com.backend.allreva.recruitment.survey.query.application.SurveyFinder;
import com.backend.allreva.recruitment.survey.query.model.CreatedSurvey;
import com.backend.allreva.recruitment.survey.query.model.JoinedSurvey;
import com.backend.allreva.recruitment.survey.query.model.SurveyDetail;
import com.backend.allreva.recruitment.survey.query.model.SurveySummary;
import com.backend.allreva.recruitment.survey.query.model.SurveyThumbnail;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/surveys")
public class SurveyQueryController implements SurveyQueryControllerSwagger {

    private final SurveyFinder surveyQueryService;

    @Override
    @GetMapping("/suggestions")
    public View<List<SurveyThumbnail>> getSurveySuggestions(@RequestParam final String query) {
        return View.onSuccess(surveyQueryService.getSurveySuggestions(query));
    }

    @Override
    @GetMapping("/search")
    public View<SliceResponse<SurveyThumbnail, Long>> searchSurveys(
            @RequestParam final String query,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final Long cursorId) {
        return View.onSuccess(surveyQueryService.searchSurveys(query, cursorId, pageSize));
    }

    @Override
    @GetMapping("/{surveyId}")
    public View<SurveyDetail> findSurveyDetail(@PathVariable(name = "surveyId") final Long surveyId) {
        return View.onSuccess(surveyQueryService.findSurveyDetail(surveyId));
    }

    @Override
    @GetMapping("/list")
    public View<List<SurveySummary>> findSurveyList(
            @RequestParam(name = "region", required = false) final Region region,
            @RequestParam(name = "sort", defaultValue = "LATEST") final SortType sortType,
            @RequestParam(name = "lastId", required = false) final Long lastId,
            @RequestParam(name = "lastEndDate", required = false) final LocalDate lastEndDate,
            @RequestParam(name = "pageSize", defaultValue = "10") final int pageSize) {
        if (lastEndDate != null && lastId == null) {
            throw new CustomException(SurveyErrorCode.SURVEY_ILLEGAL_PARAMETER);
        }
        return View.onSuccess(surveyQueryService.findSurveyList(region, sortType, lastId, lastEndDate, pageSize));
    }

    @Override
    @GetMapping("/main")
    public View<List<SurveySummary>> findSurveyMainList() {
        return View.onSuccess(surveyQueryService.findSurveyMainList());
    }

    @Override
    @GetMapping("/member/list")
    public View<List<CreatedSurvey>> findCreatedSurveyList(
            @AuthMember final Member member,
            @RequestParam(value = "lastSurveyId", required = false) final Long lastId,
            @RequestParam(name = "lastBoardingDate", required = false) final LocalDate lastBoardingDate,
            @RequestParam(value = "pageSize", defaultValue = "10") final int pageSize) {
        return View.onSuccess(
                surveyQueryService.findCreatedSurveyList(member.getId(), lastId, lastBoardingDate, pageSize));
    }

    @Override
    @GetMapping("/member/apply/list")
    public View<List<JoinedSurvey>> findJoinSurveyList(
            @AuthMember final Member member,
            @RequestParam(value = "lastSurveyParticipantId", required = false) final Long lastId,
            @RequestParam(value = "pageSize", defaultValue = "10") final int pageSize) {
        return View.onSuccess(surveyQueryService.findJoinSurveyList(member.getId(), lastId, pageSize));
    }
}
