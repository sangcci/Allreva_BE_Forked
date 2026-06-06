package com.backend.allreva.common.storage;

public interface StoragePort {

    void delete(String fileUrl);

    String generateUploadPresignedUrl(String objectKey);

    String generateDeletePresignedUrl(String fileUrl);
}
