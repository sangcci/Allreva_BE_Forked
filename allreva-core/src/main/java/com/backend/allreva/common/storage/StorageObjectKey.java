package com.backend.allreva.common.storage;

import com.backend.allreva.member.domain.Member;
import java.util.UUID;

public record StorageObjectKey(String value) {

    public static StorageObjectKey generate(
            final StoragePurpose purpose, final String originalFileName, final Member member) {
        StringBuilder keyBuilder = new StringBuilder();

        if (member != null) {
            keyBuilder.append(member.getMemberInfo().getNickname()).append("/");
        }

        keyBuilder.append(purpose).append("/");
        keyBuilder.append(UUID.randomUUID().toString().replaceAll("-", ""));
        keyBuilder.append("_").append(originalFileName);

        return new StorageObjectKey(keyBuilder.toString());
    }
}
