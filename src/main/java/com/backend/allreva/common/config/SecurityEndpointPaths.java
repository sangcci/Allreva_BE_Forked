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
        // auth
        "/api/v1/auth/**",
    };

    public static final String[] ADMIN_LIST = {"/api/v1/admin/**"};

    public static final String[] USER_LIST = {
        // member
        "/api/v1/members/**",
        // rent
        "/api/v1/rents/**",
        // survey
        "/api/v1/surveys/**"
    };

    public static final String[] ANONYMOUS_LIST = {
        // member
        "/api/v1/members/register",
        // rent
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
