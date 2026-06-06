package com.backend.allreva.common.storage;

import com.backend.allreva.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoragePresigner {

    private final StoragePort storagePort;

    public String generateUploadUrl(final StoragePurpose purpose, final String originalFileName, final Member member) {
        StorageObjectKey objectKey = StorageObjectKey.generate(purpose, originalFileName, member);
        return storagePort.generateUploadPresignedUrl(objectKey.value());
    }

    public String generateDeleteUrl(final String fileUrl) {
        return storagePort.generateDeletePresignedUrl(fileUrl);
    }
}
