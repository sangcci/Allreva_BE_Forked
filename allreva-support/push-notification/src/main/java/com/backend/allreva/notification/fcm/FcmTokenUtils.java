package com.backend.allreva.notification.fcm;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.notification.domain.NotificationErrorCode;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmTokenUtils {

    private final FcmInitializer fcmInitializer;
    private AccessToken accessToken;

    public String getAccessToken() {
        try {
            if (accessToken != null && !isTokenExpired(accessToken)) {
                log.debug("Reusing cached FCM access token. expires: {}", accessToken.getExpirationTime());
                return accessToken.getTokenValue();
            }

            GoogleCredentials credentials = fcmInitializer.getCredentials();
            credentials.refreshIfExpired();
            accessToken = credentials.getAccessToken();
            log.info("New FCM access token issued. expires: {}", accessToken.getExpirationTime());
            return accessToken.getTokenValue();

        } catch (IOException e) {
            log.error("Failed to issue FCM access token - Google OAuth2 token refresh failed", e);
            throw new CustomException(NotificationErrorCode.FCM_TOKEN_GENERATION_FAILED, e);
        }
    }

    private boolean isTokenExpired(AccessToken token) {
        return token.getExpirationTime().toInstant().isBefore(Instant.now());
    }
}
