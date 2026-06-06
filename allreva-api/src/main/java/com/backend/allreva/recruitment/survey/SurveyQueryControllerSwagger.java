package com.backend.allreva.recruitment.survey;

import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.common.web.response.View;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.recruitment.survey.domain.Region;
import com.backend.allreva.recruitment.survey.domain.SortType;
import com.backend.allreva.recruitment.survey.query.model.CreatedSurvey;
import com.backend.allreva.recruitment.survey.query.model.JoinedSurvey;
import com.backend.allreva.recruitment.survey.query.model.SurveyDetail;
import com.backend.allreva.recruitment.survey.query.model.SurveySummary;
import com.backend.allreva.recruitment.survey.query.model.SurveyThumbnail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "수요조사 API", description = "수요조사 Query API")
public interface SurveyQueryControllerSwagger {

    @Operation(summary = "수요조사 자동완성 제안", description = "검색어 관련도 상위 2개")
    View<List<SurveyThumbnail>> getSurveySuggestions(String query);

    @Operation(summary = "수요조사 검색 목록 조회", description = "무한 스크롤. 관련도 순 정렬")
    View<SliceResponse<SurveyThumbnail, Long>> searchSurveys(String query, @Min(1) int pageSize, Long cursorId);

    @Operation(summary = "수요조사 상세 조회")
    View<SurveyDetail> findSurveyDetail(Long surveyId);

    @Operation(summary = "수요조사 목록 조회", description = "무한 스크롤. CLOSING 정렬 시 lastId + lastEndDate 모두 필요")
    View<List<SurveySummary>> findSurveyList(
            Region region, SortType sortType, Long lastId, LocalDate lastEndDate, @Min(10) int pageSize);

    @Operation(summary = "메인 수요조사 목록 조회", description = "가장 가까운 콘서트 순 5개")
    View<List<SurveySummary>> findSurveyMainList();

    @SecurityRequirement(name = "USER")
    @Operation(summary = "내가 개설한 수요조사 목록 조회", description = "**[HOST]** 무한 스크롤")
    View<List<CreatedSurvey>> findCreatedSurveyList(
            Member member, Long lastId, LocalDate lastBoardingDate, @Min(10) int pageSize);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "내가 참여한 수요조사 목록 조회", description = "**[PARTICIPANT]** 무한 스크롤")
    View<List<JoinedSurvey>> findJoinSurveyList(Member member, Long lastId, @Min(10) int pageSize);
}
