package com.backend.allreva.notification.command.implementation;

import java.util.List;
import java.util.Optional;

public interface NotificationTargetReader {

    Optional<String> get(Long memberId);

    List<String> getAll(List<Long> memberIds);
}
