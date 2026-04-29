package com.backend.allreva.module.recruitment.survey.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.member.domain.Member;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "수요조사 API", description = "수요조사 관련 API")
public interface SurveyControllerSwagger {

    @SecurityRequirement(name = "USER")
    @Operation(summary = "수요조사 개설", description = "**[HOST]**")
    Response<Long> openSurvey(Member member, @Valid OpenSurveyRequest openSurveyRequest);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "수요조사 수정", description = "**[HOST]**")
    Response<Void> updateSurvey(Member member, @Valid UpdateSurveyRequest updateSurveyRequest);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "수요조사 삭제", description = "**[HOST]**")
    Response<Void> removeSurvey(Member member, SurveyIdRequest surveyIdRequest);

    @Operation(summary = "수요조사 상세 조회")
    Response<SurveyDetailResponse> findSurveyDetail(Long surveyId);

    @Operation(summary = "수요조사 목록 조회", description = "무한 스크롤. CLOSING 정렬 시 lastId + lastEndDate 모두 필요")
    Response<List<SurveySummaryResponse>> findSurveyList(
            Region region, SortType sortType, Long lastId, LocalDate lastEndDate, @Min(10) int pageSize);

    @Operation(summary = "메인 수요조사 목록 조회", description = "가장 가까운 콘서트 순 5개")
    Response<List<SurveySummaryResponse>> findSurveyMainList();

    @SecurityRequirement(name = "USER")
    @Operation(summary = "수요조사 참여", description = "**[PARTICIPANT]**")
    Response<Long> joinSurvey(Member member, @Valid JoinSurveyRequest joinSurveyRequest);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "수요조사 참여 취소", description = "**[PARTICIPANT]**")
    Response<Void> cancelJoin(Member member, Long participantId);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "내가 개설한 수요조사 목록 조회", description = "**[HOST]** 무한 스크롤")
    Response<List<CreatedSurveyResponse>> findCreatedSurveyList(
            Member member, Long lastId, LocalDate lastBoardingDate, @Min(10) int pageSize);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "내가 참여한 수요조사 목록 조회", description = "**[PARTICIPANT]** 무한 스크롤")
    Response<List<JoinSurveyResponse>> findJoinSurveyList(Member member, Long lastId, @Min(10) int pageSize);
}
