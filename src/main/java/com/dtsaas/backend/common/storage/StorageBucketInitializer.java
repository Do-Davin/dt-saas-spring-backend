package com.dtsaas.backend.common.storage;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class StorageBucketInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StorageBucketInitializer.class);

    private final StorageService storageService;
    private final StorageProperties props;

    @Override
    public void run(ApplicationArguments args) {
        if (!props.autoCreateBucket()) {
            log.info("Storage bucket auto-create disabled for bucket {}", props.bucket());
            return;
        }

        try {
            if (storageService.bucketExists()) {
                log.info("Storage bucket {} already exists", props.bucket());
                return;
            }

            storageService.createBucket();
            log.info("Created local storage bucket {}", props.bucket());
        } catch (StorageException e) {
            log.error("Storage bucket initialization skipped: {}", e.getDetail());
        }
    }
}
