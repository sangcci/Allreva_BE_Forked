package com.backend.allreva.notification.command.implementation;

public interface NotificationTargetWriter {

    void save(Long memberId, String target);

    void delete(Long memberId);
}
