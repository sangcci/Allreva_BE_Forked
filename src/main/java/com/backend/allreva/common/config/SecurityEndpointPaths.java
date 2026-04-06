package com.backend.allreva.common.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityEndpointPaths {

    public static final String[] WHITE_LIST = {
        "/h2-console/**",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-resources/**",
        // actuator
        "/actuator/**",
        // auth
        "/api/v1/auth/**",
        // test (staging only - prod에서는 @Profile("!prod")로 컨트롤러 자체가 없음)
        "/api/test/**",
    };

    public static final String[] ADMIN_LIST = {"/api/v1/admin/**"};

    public static final String[] USER_LIST = {
        // member
        "/api/v1/members/**",
        // rent - write (host)
        "/api/v1/rents",
        "/api/v1/rents/close",
        // rent - host read
        "/api/v1/rents/host/**",
        // rent - participant
        "/api/v1/rents/join/**",
        // rent - member detail overlay
        "/api/v1/rents/*/me",
        // rent - deposit account (merge to host detail 예정)
        "/api/v1/rents/*/deposit-account",
        // survey
        "/api/v1/surveys/**"
    };

    public static final String[] ANONYMOUS_LIST = {
        // member
        "/api/v1/members/register",
        // rent - public read
        "/api/v1/rents/{id:\\d+}",
        "/api/v1/rents/main",
        "/api/v1/rents/list",
        // survey
        "/api/v1/surveys/{surveyId:\\d+}",
        "/api/v1/surveys/main",
        "/api/v1/surveys/list",
        // concert
        "/api/v1/concerts/**",
        // place
        "/api/v1/places/**",
        // search
        "/api/v1/search/**",
        "/api/v1/artists/search",
        "/api/v1/file/**",
    };
}
