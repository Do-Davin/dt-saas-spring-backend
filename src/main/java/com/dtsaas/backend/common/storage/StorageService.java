package com.dtsaas.backend.common.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final S3Client s3Client;
    private final StorageProperties props;

    public void uploadObject(String key, InputStream inputStream, long size, String contentType) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(props.bucket())
                        .key(key)
                        .contentType(contentType)
                        .contentLength(size)
                        .build(),
                RequestBody.fromInputStream(inputStream, size));
    }

    public void deleteObject(String key) {
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(props.bucket())
                        .key(key)
                        .build());
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
            throw e;
        }
    }

    public String buildProductImageKey(UUID businessId, UUID productId, UUID imageId, String extension) {
        return String.format("businesses/%s/products/%s/%s.%s", businessId, productId, imageId, extension);
    }
}
