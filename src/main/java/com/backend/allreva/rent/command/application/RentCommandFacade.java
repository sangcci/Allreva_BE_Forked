package com.backend.allreva.rent.command.application;

import com.backend.allreva.module.recruitment.chat.application.GroupChatService;
import com.backend.allreva.module.recruitment.chat.application.dto.AddGroupChatRequest;
import com.backend.allreva.common.event.Events;
import com.backend.allreva.module.notification.domain.event.NotificationEvent;
import com.backend.allreva.module.notification.domain.value.NotificationType;
import com.backend.allreva.rent.command.application.request.RentIdRequest;
import com.backend.allreva.rent.command.application.request.RentRegisterRequest;
import com.backend.allreva.rent.command.application.request.RentUpdateRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RentCommandFacade {

    private final RentCommandService rentCommandService;
    private final GroupChatService groupChatService;

    public Long registerRent(
            final RentRegisterRequest rentRegisterRequest,
            final Long memberId) {
        // register rent
        Long rentId = rentCommandService.registerRent(rentRegisterRequest, memberId);

        // create group chat
        groupChatService.add(
                new AddGroupChatRequest(
                        rentRegisterRequest.title(),
                        rentRegisterRequest.maxPassenger()),
                rentRegisterRequest.image(),
                memberId);

        // push notification
        Events.raise(NotificationEvent.builder()
                .type(NotificationType.RENT_REGISTERED)
                .recipientIds(List.of(memberId))
                .senderId(memberId)
                .roomId(rentId)
                .roomName(rentRegisterRequest.title())
                .content(rentRegisterRequest.title() + " 차량 대절이 등록되었습니다.")
                .build());

        return rentId;
    }

    public void updateRent(
            final RentUpdateRequest rentUpdateRequest,
            final Long memberId) {
        rentCommandService.updateRent(rentUpdateRequest, memberId);
    }

    public void closeRent(
            final RentIdRequest rentIdRequest,
            final Long memberId) {
        rentCommandService.closeRent(rentIdRequest, memberId);
    }

    public void deleteRent(
            final RentIdRequest rentIdRequest,
            final Long memberId) {
        rentCommandService.deleteRent(rentIdRequest, memberId);
    }
}
