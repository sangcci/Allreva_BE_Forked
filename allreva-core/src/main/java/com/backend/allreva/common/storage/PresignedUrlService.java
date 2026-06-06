package com.backend.allreva.common.storage;

import com.backend.allreva.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PresignedUrlService {

    private final StoragePresigner storagePresigner;

    public String generateUploadUrl(final PresignedUrlRequest request, final Member member) {
        return storagePresigner.generateUploadUrl(request.purpose(), request.fileName(), member);
    }

    public String generateDeleteUrl(final String fileUrl) {
        return storagePresigner.generateDeleteUrl(fileUrl);
    }
}
