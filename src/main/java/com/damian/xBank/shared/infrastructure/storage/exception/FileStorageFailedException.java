package com.damian.xBank.shared.infrastructure.storage.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class FileStorageFailedException extends FileStorageException {

    public FileStorageFailedException(String path, String filename) {
        super(ErrorCodes.STORAGE_FILE_FAILED, path, filename);
    }

}
