package com.damian.xBank.shared.infrastructure.storage.exception;

public class FileStorageFailedException extends FileStorageException {

    public FileStorageFailedException(String message, String path, String filename) {
        super(message, path, filename);
    }

    public FileStorageFailedException(String message, String path) {
        this(message, path, null);
    }

    public FileStorageFailedException(String message) {
        this(message, null);
    }

}
