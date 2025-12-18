package com.damian.xBank.infrastructure.storage.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class FileStorageNotFoundException extends FileStorageException {

    public FileStorageNotFoundException(String path, String fileName) {
        super(ErrorCodes.STORAGE_FILE_NOT_FOUND, path, fileName);
    }

}
