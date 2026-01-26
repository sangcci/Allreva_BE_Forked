package com.backend.allreva.module.chat.presentation;

import com.backend.allreva.module.chat.application.GroupChatService;
import com.backend.allreva.module.chat.application.dto.DeleteGroupChatRequest;
import com.backend.allreva.module.chat.application.dto.JoinGroupChatRequest;
import com.backend.allreva.module.chat.application.dto.LeaveGroupChatRequest;
import com.backend.allreva.module.chat.application.dto.UpdateGroupChatRequest;
import com.backend.allreva.module.chat.application.dto.GroupChatDetailResponse;
import com.backend.allreva.module.chat.application.dto.GroupChatOverviewResponse;
import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.auth.security.AuthMember;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/chat/group")
@RestController
public class GroupChatController {

    private final GroupChatService groupChatService;

    @GetMapping("/{groupChatId}")
    public Response<GroupChatDetailResponse> findGroupChatInformation(
            @PathVariable("groupChatId") final Long groupChatId,
            @AuthMember final Member member) {
        GroupChatDetailResponse response = groupChatService.findGroupChatInfo(member.getId(), groupChatId);
        return Response.onSuccess(response);
    }

    @GetMapping("/invitation/{groupChatId}")
    public Response<String> findInviteUrl(
            @PathVariable("groupChatId") final Long groupChatId,
            @AuthMember final Member member) {
        String inviteCode = groupChatService
                .findInviteCode(member.getId(), groupChatId);
        return Response.onSuccess(inviteCode);
    }

    @PatchMapping
    public Response<Void> updateGroupChat(
            @RequestBody final UpdateGroupChatRequest request,
            @AuthMember final Member member) {
        groupChatService.update(
                request,
                member.getId());
        return Response.onSuccess();
    }

    @GetMapping("/join/{uuid}")
    public Response<GroupChatOverviewResponse> findGroupChatOverview(
            @PathVariable("uuid") final String uuid) {
        GroupChatOverviewResponse response = groupChatService.findOverview(uuid);
        return Response.onSuccess(response);
    }

    @PostMapping("/join")
    public Response<Long> joinGroupChat(
            @RequestBody final JoinGroupChatRequest request,
            @AuthMember final Member member) {
        Long groupChatId = groupChatService
                .join(request.uuid(), member.getId());
        return Response.onSuccess(groupChatId);
    }

    @DeleteMapping("/leave")
    public Response<Void> leaveGroupChat(
            @RequestBody final LeaveGroupChatRequest request,
            @AuthMember final Member member) {
        groupChatService
                .leave(request.groupChatId(), member.getId());
        return Response.onSuccess();
    }

    @DeleteMapping
    public Response<Void> deleteGroupChat(
            @RequestBody final DeleteGroupChatRequest request,
            @AuthMember final Member member) {
        groupChatService.delete(
                request.groupChatId(),
                member.getId());
        return Response.onSuccess();
    }

}
