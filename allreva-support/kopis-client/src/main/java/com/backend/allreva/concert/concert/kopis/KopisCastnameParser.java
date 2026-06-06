package com.backend.allreva.concert.concert.kopis;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
