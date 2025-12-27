package com.damian.xBank.shared.infrastructure.storage.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class FileStorageException extends ApplicationException {
    public FileStorageException(String message, String path, String fileName) {
        super(message, fileName, new Object[]{path, fileName});
    }
}
