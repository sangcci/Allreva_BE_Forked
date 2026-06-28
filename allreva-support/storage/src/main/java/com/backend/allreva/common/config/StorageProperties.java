package com.backend.allreva.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;

@ConfigurationProperties(prefix = "spring.cloud.aws")
public record StorageProperties(S3 s3, Credentials credentials, Region region) {

    public record S3(String bucket, String endpoint) {}

    public record Credentials(String accessKey, String secretKey) {}

    public record Region(@Name("static") String staticRegion) {}
}
