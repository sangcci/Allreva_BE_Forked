package com.backend.allreva.module.recruitment.chat.domain;

import java.util.UUID;

import com.backend.allreva.module.recruitment.chat.domain.event.ChatDeletedEvent;
import com.backend.allreva.module.recruitment.chat.domain.event.ChatMemberJoinedEvent;
import com.backend.allreva.module.recruitment.chat.domain.event.ChatMemberLeftEvent;
import com.backend.allreva.module.recruitment.chat.domain.event.ChatUpdatedEvent;
import com.backend.allreva.module.recruitment.chat.domain.value.Description;
import com.backend.allreva.module.recruitment.chat.domain.value.Title;
import com.backend.allreva.module.recruitment.chat.exception.ChattingErrorCode;
import com.backend.allreva.common.event.Events;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.model.Image;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(columnList = "manager_id"))
@Entity
public class GroupChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID uuid;

    private Long managerId;

    @Embedded
    private Title title;
    @Embedded
    private Description description;
    @Embedded
    private Image thumbnail;

    private int headcount;
    private int capacity;

    @Builder
    public GroupChat(
            final Long managerId,
            final String title,
            final Image thumbnail,
            final int capacity) {
        this.uuid = UUID.randomUUID();
        this.managerId = managerId;
        this.title = new Title(title);
        this.description = new Description("");
        this.thumbnail = thumbnail;
        this.headcount = 1;
        this.capacity = capacity;
    }

    public void updateInfo(
            final Long managerId,
            final String title,
            final String description,
            final Image thumbnail) {
        validateManager(managerId);
        this.managerId = managerId;
        this.title = new Title(title);
        this.description = new Description(description);
        this.thumbnail = thumbnail;

        ChatUpdatedEvent updatedEvent = new ChatUpdatedEvent(
                managerId,
                this.id,
                title,
                thumbnail);
        Events.raise(updatedEvent);
    }

    public void validateForDelete(final Long memberId) {
        validateManager(memberId);
        if (this.headcount != 1) {
            throw new CustomException(ChattingErrorCode.DO_NOT_MEET_CONDITIONS_TO_DELETE);
        }

        ChatDeletedEvent deletedEvent = new ChatDeletedEvent(this.id, memberId);
        Events.raise(deletedEvent);
    }

    public void validateManager(final Long memberId) {
        if (this.managerId.equals(memberId)) {
            return;
        }
        throw new CustomException(ChattingErrorCode.INVALID_MANAGER);
    }

    public void addHeadcount(final Long memberId) {
        this.headcount++;

        ChatMemberJoinedEvent joinedEvent = new ChatMemberJoinedEvent(memberId, this.id);
        Events.raise(joinedEvent);
    }

    public void subtractHeadcount(final Long memberId) {
        this.headcount--;

        ChatMemberLeftEvent leavedEvent = new ChatMemberLeftEvent(memberId, this.id);
        Events.raise(leavedEvent);
    }

}
