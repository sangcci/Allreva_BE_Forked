package com.backend.allreva.common.config;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(StorageProperties.class)
public class S3Config {

    private final StorageProperties storageProperties;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = credentials();
        var builder =
                S3Client.builder().region(region()).credentialsProvider(StaticCredentialsProvider.create(credentials));

        if (hasEndpointOverride()) {
            builder.endpointOverride(endpointOverride())
                    .serviceConfiguration(S3Configuration.builder()
                            .pathStyleAccessEnabled(true)
                            .build());
        }

        return builder.build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        AwsBasicCredentials credentials = credentials();
        var builder = S3Presigner.builder()
                .region(region())
                .credentialsProvider(StaticCredentialsProvider.create(credentials));

        if (hasEndpointOverride()) {
            builder.endpointOverride(endpointOverride())
                    .serviceConfiguration(S3Configuration.builder()
                            .pathStyleAccessEnabled(true)
                            .build());
        }

        return builder.build();
    }

    private AwsBasicCredentials credentials() {
        return AwsBasicCredentials.create(
                storageProperties.credentials().accessKey(),
                storageProperties.credentials().secretKey());
    }

    private Region region() {
        return Region.of(storageProperties.region().staticRegion());
    }

    private boolean hasEndpointOverride() {
        return storageProperties.s3().endpoint() != null
                && !storageProperties.s3().endpoint().isBlank();
    }

    private URI endpointOverride() {
        return URI.create(storageProperties.s3().endpoint());
    }
}
