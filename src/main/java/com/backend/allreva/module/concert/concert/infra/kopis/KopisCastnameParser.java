package com.backend.allreva.module.concert.concert.infra.kopis;

import java.util.List;

public final class KopisCastnameParser {

    public static List<String> parseCastNames(final String prfcast) {
        if (prfcast == null || prfcast.isBlank()) {
            return List.of();
        }
        return java.util.Arrays.stream(prfcast.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
