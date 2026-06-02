package com.dtsaas.backend.common.storage;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StorageConfig {

        @Bean
        public S3Client s3Client(StorageProperties props) {
                return S3Client.builder()
                                .endpointOverride(URI.create(props.endpoint()))
                                .region(Region.of(props.region()))
                                .credentialsProvider(StaticCredentialsProvider.create(
                                                AwsBasicCredentials.create(props.accessKey(), props.secretKey())))
                                .httpClientBuilder(UrlConnectionHttpClient.builder())
                                .serviceConfiguration(S3Configuration.builder()
                                                .pathStyleAccessEnabled(true)
                                                .build())
                                .build();
        }
}
