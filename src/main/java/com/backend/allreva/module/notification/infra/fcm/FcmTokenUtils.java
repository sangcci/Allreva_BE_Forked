package com.backend.allreva.module.notification.infra.fcm;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;

import org.springframework.core.io.ClassPathResource;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.notification.exception.NotificationErrorCode;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class FcmTokenUtils {

    private static final String KEY_PATH = "firebase/firebase-service-account.json";
    private static final GoogleCredentials googleCredentials;
    private static AccessToken accessToken;

    static {
        try (InputStream serviceAccount = new ClassPathResource(KEY_PATH).getInputStream()) {
            googleCredentials = GoogleCredentials.fromStream(serviceAccount)
                    .createScoped(List.of("https://www.googleapis.com/auth/firebase.messaging"));
        } catch (IOException e) {
            log.error("Firebase 인증 정보 초기화 실패 - 서비스 계정 키 파일을 확인하세요: {}", KEY_PATH, e);
            throw new RuntimeException("Failed to initialize GoogleCredentials", e);
        }
    }

    public static String getAccessToken() {
        try {
            // 기존 캐시된 토큰이 존재하고, 만료되지 않았다면 재사용
            if (accessToken != null && !isTokenExpired(accessToken)) {
                log.debug("기존 FCM 엑세스 토큰 재사용. 만료 시간: {}", accessToken.getExpirationTime());
                return accessToken.getTokenValue();
            }

            // 토큰 갱신
            googleCredentials.refreshIfExpired();
            accessToken = googleCredentials.getAccessToken();
            log.info("새 FCM 엑세스 토큰 발급 완료. 만료 시간: {}", accessToken.getExpirationTime());
            return accessToken.getTokenValue();

        } catch (IOException e) {
            log.error("FCM 엑세스 토큰 발급 실패 - Google OAuth2 토큰 갱신 실패", e);
            throw new CustomException(NotificationErrorCode.FCM_TOKEN_GENERATION_FAILED, e);
        }
    }

    private static boolean isTokenExpired(AccessToken token) {
        return token.getExpirationTime().toInstant().isBefore(Instant.now());
    }
}
