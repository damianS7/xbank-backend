package com.damian.xBank.shared.infrastructure.storage.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class ImageUploadFailedException extends ApplicationException {
    private final String path;

    public ImageUploadFailedException(String message, String path) {
        super(message);
        this.path = path;
    }

    public ImageUploadFailedException(String message) {
        this(message, null);
    }

    public String getPath() {
        return path;
    }
}
