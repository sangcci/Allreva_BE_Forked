package com.backend.allreva.notification.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmInitializer {

    private static final List<String> FCM_SCOPES = List.of("https://www.googleapis.com/auth/firebase.messaging");

    private final ResourceLoader resourceLoader;
    private final FcmProperties fcmProperties;

    private GoogleCredentials credentials;

    @PostConstruct
    public void initialize() {
        Resource resource = resourceLoader.getResource(fcmProperties.serviceAccountKey());
        try (InputStream serviceAccount = resource.getInputStream()) {
            credentials = GoogleCredentials.fromStream(serviceAccount).createScoped(FCM_SCOPES);

            FirebaseOptions options =
                    FirebaseOptions.builder().setCredentials(credentials).build();
            FirebaseApp.initializeApp(options);
            log.info("Firebase initialized - key: {}", fcmProperties.serviceAccountKey());
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }

    public GoogleCredentials getCredentials() {
        return credentials;
    }
}
