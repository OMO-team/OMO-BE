package com.omo.backend.global.storage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "aws.s3")
public record S3Properties(
        @NotBlank String region,
        @NotBlank String profileBucket,
        @NotBlank String inquiryBucket,
        @NotNull Duration presignedUrlExpiration
) {}
