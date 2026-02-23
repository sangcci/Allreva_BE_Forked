package com.backend.allreva.module.recruitment.chat.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.recruitment.chat.application.dto.DeleteGroupChatRequest;
import com.backend.allreva.module.recruitment.chat.application.dto.GroupChatDetailResponse;
import com.backend.allreva.module.recruitment.chat.application.dto.GroupChatOverviewResponse;
import com.backend.allreva.module.recruitment.chat.application.dto.JoinGroupChatRequest;
import com.backend.allreva.module.recruitment.chat.application.dto.LeaveGroupChatRequest;
import com.backend.allreva.module.recruitment.chat.application.dto.UpdateGroupChatRequest;
import com.backend.allreva.module.member.domain.Member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "그룹 채팅 API", description = "그룹 채팅 API")
public interface GroupChatControllerSwagger {

    @Operation(summary = "그룹 채팅 상세 조회", description = "그룹 채팅방 상세 정보 조회")
    Response<GroupChatDetailResponse> findGroupChatInformation(Long groupChatId, Member member);

    @Operation(summary = "초대 URL 조회", description = "그룹 채팅방 초대 코드 조회")
    Response<String> findInviteUrl(Long groupChatId, Member member);

    @Operation(summary = "그룹 채팅 수정", description = "그룹 채팅방 정보 수정")
    Response<Void> updateGroupChat(UpdateGroupChatRequest request, Member member);

    @Operation(summary = "그룹 채팅 미리보기 조회", description = "초대 링크를 통한 그룹 채팅방 미리보기")
    Response<GroupChatOverviewResponse> findGroupChatOverview(String uuid);

    @Operation(summary = "그룹 채팅 참여", description = "초대 코드로 그룹 채팅방 참여")
    Response<Long> joinGroupChat(JoinGroupChatRequest request, Member member);

    @Operation(summary = "그룹 채팅 나가기", description = "그룹 채팅방 나가기")
    Response<Void> leaveGroupChat(LeaveGroupChatRequest request, Member member);

    @Operation(summary = "그룹 채팅 삭제", description = "그룹 채팅방 삭제 (방장만 가능)")
    Response<Void> deleteGroupChat(DeleteGroupChatRequest request, Member member);
}
