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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
@Tag(name = "수요조사 API Controller")
public class SurveyController {

    private final SurveyService surveyService;

    @Operation(summary = "수요조사 개설 API", description = "수요조사를 개설합니다.")
    @PostMapping
    public Response<Long> openSurvey(
            @AuthMember Member member, @Valid @RequestBody OpenSurveyRequest openSurveyRequest) {
        return Response.onSuccess(surveyService.openSurvey(member.getId(), openSurveyRequest));
    }

    @Operation(summary = "수요조사 수정 API", description = "수요조사를 수정합니다.")
    @PatchMapping
    public Response<Void> updateSurvey(
            @AuthMember Member member, @Valid @RequestBody UpdateSurveyRequest updateSurveyRequest) {
        surveyService.updateSurvey(member.getId(), updateSurveyRequest);
        return Response.onSuccess();
    }

    @Operation(summary = "수요조사 삭제 API", description = "수요조사를 삭제합니다.")
    @DeleteMapping
    public Response<Void> removeSurvey(@AuthMember Member member, @RequestBody SurveyIdRequest surveyIdRequest) {
        surveyService.removeSurvey(member.getId(), surveyIdRequest);
        return Response.onSuccess();
    }

    @Operation(summary = "수요조사 상세 조회 API", description = "수요조사를 상세조회합니다.")
    @GetMapping("/{surveyId}")
    public Response<SurveyDetailResponse> findSurveyDetail(@PathVariable(name = "surveyId") Long surveyId) {
        return Response.onSuccess(surveyService.findSurveyDetail(surveyId));
    }

    @Operation(
            summary = "수요조사 목록 조회 API",
            description = "수요조사 목록을 조회합니다.\n\n"
                    + "- <b>정렬 옵션</b>:\n"
                    + "  - 최신순 (<b>LATEST</b>): lastId를 마지막 항목의 ID로 전달. lastEndDate는 주지마세요.\n"
                    + "  - 오래된 순 (<b>OLDEST</b>): lastId를 마지막 항목의 ID로 전달. lastEndDate는 주지마세요.\n"
                    + "  - 마감 임박순 (<b>CLOSING</b>): 마지막 항목의 lastId와 lastEndDate를 전달하며, 둘 모두 필수입니다.\n\n"
                    + "- <b>첫 페이지 요청</b>: lastId와 lastEndDate를 전달하지 않으면 됩니다.\n"
                    + "- <b>기본값</b>:\n"
                    + "  - <b>sort</b>: 최신순 (LATEST)\n"
                    + "  - <b>pageSize</b>: 10\n"
                    + "  - <b>region</b>: 전체 조회")
    @GetMapping("/list")
    public Response<List<SurveySummaryResponse>> findSurveyList(
            @RequestParam(name = "region", required = false) final Region region,
            @RequestParam(name = "sort", defaultValue = "LATEST") final SortType sortType,
            @RequestParam(name = "lastId", required = false) final Long lastId,
            @RequestParam(name = "lastEndDate", required = false) final LocalDate lastEndDate,
            @Min(10) @RequestParam(name = "pageSize", defaultValue = "10") final int pageSize) {
        if (lastEndDate != null && lastId == null) {
            throw new CustomException(SurveyErrorCode.SURVEY_ILLEGAL_PARAMETER);
        }
        List<SurveySummaryResponse> responses =
                surveyService.findSurveyList(region, sortType, lastId, lastEndDate, pageSize);
        return Response.onSuccess(responses);
    }

    @GetMapping("/main")
    @Operation(summary = "첫 화면 survey API 입니다.", description = "첫 화면 survey API 입니다. 현재 날짜에서 가장 가까운 콘서트 순으로 5개 정렬")
    public Response<List<SurveySummaryResponse>> findSurveyMainList() {
        return Response.onSuccess(surveyService.findSurveyMainList());
    }

    @Operation(summary = "수요조사 응답 제출 API", description = "수요조사에 대한 응답을 제출합니다.")
    @PostMapping("/apply")
    public Response<Long> joinSurvey(
            @AuthMember Member member, @Valid @RequestBody JoinSurveyRequest joinSurveyRequest) {
        return Response.onSuccess(surveyService.joinSurvey(member.getId(), joinSurveyRequest));
    }

    @Operation(summary = "수요조사 참여 취소 API", description = "수요조사 참여를 취소합니다.")
    @DeleteMapping("/apply/{participantId}")
    public Response<Void> cancelJoin(
            @AuthMember Member member, @PathVariable(name = "participantId") Long participantId) {
        surveyService.cancelJoin(member.getId(), participantId);
        return Response.onSuccess();
    }

    @Operation(
            summary = "내가 개설한 수요조사 목록 조회 API",
            description = "첫페이지는 lastSurveyId 주지 않으셔도됩니다. 다음페이지부터는 마지막요소의 id 넣어주세요.\ndefault page size는 10입니다.")
    @GetMapping("/member/list")
    public Response<List<CreatedSurveyResponse>> findCreatedSurveyList(
            @AuthMember Member member,
            @RequestParam(value = "lastSurveyId", required = false) final Long lastId,
            @RequestParam(name = "lastBoardingDate", required = false) final LocalDate lastBoardingDate,
            @Min(10) @RequestParam(value = "pageSize", defaultValue = "10") final int pageSize) {
        return Response.onSuccess(
                surveyService.findCreatedSurveyList(member.getId(), lastId, lastBoardingDate, pageSize));
    }

    @Operation(
            summary = "내가 참여한 수요조사 목록 조회 API",
            description =
                    "첫페이지는 lastSurveyParticipantId 주지 않으셔도됩니다. 다음페이지부터는 마지막요소의 id 넣어주세요.\ndefault page size는 10입니다.")
    @GetMapping("/member/apply/list")
    public Response<List<JoinSurveyResponse>> findJoinSurveyList(
            @AuthMember Member member,
            @RequestParam(value = "lastSurveyParticipantId", required = false) final Long lastId,
            @Min(10) @RequestParam(value = "pageSize", defaultValue = "10") final int pageSize) {
        return Response.onSuccess(surveyService.findJoinSurveyList(member.getId(), lastId, pageSize));
    }
}
