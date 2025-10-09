package com.damian.whatsapp.shared.infrastructure.storage.exception;

import com.damian.whatsapp.shared.exception.ApplicationException;

public class FileStorageException extends ApplicationException {
    private final String path;
    private final String fileName;

    public FileStorageException(String message, String path, String fileName) {
        super(message);
        this.path = path;
        this.fileName = fileName;
    }

    public FileStorageException(String message) {
        this(message, null, null);
    }

    public String getFileName() {
        return fileName;
    }

    public String getPath() {
        return path;
    }
}
