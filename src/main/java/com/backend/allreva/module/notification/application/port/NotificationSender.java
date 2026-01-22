package com.backend.allreva.module.notification.application.port;

public interface NotificationSender {

    void sendMessage(String target, String title, String message);
}
