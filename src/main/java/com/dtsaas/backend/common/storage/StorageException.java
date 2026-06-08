package com.dtsaas.backend.common.storage;

public class StorageException extends RuntimeException {

    private final String detail;

    public StorageException(String detail, Throwable cause) {
        super("Storage is not configured correctly", cause);
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }
}
