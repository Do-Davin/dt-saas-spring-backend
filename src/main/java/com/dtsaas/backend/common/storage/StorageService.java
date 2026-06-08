package com.dtsaas.backend.common.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {

    private static final Duration READ_URL_EXPIRY = Duration.ofMinutes(60);

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final StorageProperties props;

    public void uploadObject(String key, InputStream inputStream, long size, String contentType) {
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(props.bucket())
                            .key(key)
                            .contentType(contentType)
                            .contentLength(size)
                            .build(),
                    RequestBody.fromInputStream(inputStream, size));
        } catch (SdkException e) {
            throw storageException("Storage upload failed", e);
        }
    }

    public void deleteObject(String key) {
        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(props.bucket())
                            .key(key)
                            .build());
        } catch (SdkException e) {
            throw storageException("Storage delete failed", e);
        }
    }

    public boolean objectExists(String key) {
        try {
            s3Client.headObject(
                    HeadObjectRequest.builder()
                            .bucket(props.bucket())
                            .key(key)
                            .build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            if (e.statusCode() == 404)
                return false;
            throw storageException("Storage object check failed", e);
        } catch (SdkException e) {
            throw storageException("Storage object check failed", e);
        }
    }

    public boolean bucketExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder()
                    .bucket(props.bucket())
                    .build());
            return true;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }
            throw storageException("Storage bucket check failed", e);
        } catch (SdkException e) {
            throw storageException("Storage bucket check failed", e);
        }
    }

    public void createBucket() {
        try {
            s3Client.createBucket(CreateBucketRequest.builder()
                    .bucket(props.bucket())
                    .build());
        } catch (SdkException e) {
            throw storageException("Storage bucket creation failed", e);
        }
    }

    public String buildProductImageKey(UUID businessId, UUID productId, UUID imageId, String extension) {
        return String.format("businesses/%s/products/%s/%s.%s", businessId, productId, imageId, extension);
    }

    public String generateReadUrl(String objectKey) {
        try {
            return s3Presigner.presignGetObject(r -> r
                    .signatureDuration(READ_URL_EXPIRY)
                    .getObjectRequest(g -> g
                            .bucket(props.bucket())
                            .key(objectKey)))
                    .url().toString();
        } catch (SdkException e) {
            throw storageException("Storage read URL generation failed", e);
        }
    }

    private StorageException storageException(String action, SdkException e) {
        String detail;
        if (e instanceof S3Exception s3Exception && s3Exception.statusCode() == 404) {
            detail = "%s: bucket %s not found".formatted(action, props.bucket());
        } else {
            detail = "%s for bucket %s failed: %s"
                    .formatted(action, props.bucket(), e.getMessage());
        }
        return new StorageException(detail, e);
    }
}
