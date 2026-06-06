package com.backend.allreva.common.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StorageWriter {

    private final StoragePort storagePort;

    public void delete(final String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        storagePort.delete(fileUrl);
    }
}
