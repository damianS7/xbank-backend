package com.damian.xBank.infrastructure.storage.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class FileStorageFailedException extends FileStorageException {

    public FileStorageFailedException(String path, String filename) {
        super(Exceptions.STORAGE_FILE_FAILED, path, filename);
    }

}
