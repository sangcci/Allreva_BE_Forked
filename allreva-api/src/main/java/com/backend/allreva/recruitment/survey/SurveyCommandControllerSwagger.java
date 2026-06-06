package com.backend.allreva.recruitment.survey;

import com.backend.allreva.common.web.response.View;
import com.backend.allreva.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "수요조사 API", description = "수요조사 Command API")
public interface SurveyCommandControllerSwagger {

    @SecurityRequirement(name = "USER")
    @Operation(summary = "수요조사 개설", description = "**[HOST]**")
    View<Long> openSurvey(Member member, OpenSurveyRequest request);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "수요조사 수정", description = "**[HOST]**")
    View<Void> updateSurvey(Member member, UpdateSurveyRequest request);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "수요조사 삭제", description = "**[HOST]**")
    View<Void> removeSurvey(Member member, SurveyIdRequest request);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "수요조사 참여", description = "**[PARTICIPANT]**")
    View<Long> joinSurvey(Member member, JoinSurveyRequest request);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "수요조사 참여 취소", description = "**[PARTICIPANT]**")
    View<Void> cancelJoin(Member member, Long participantId);
}
