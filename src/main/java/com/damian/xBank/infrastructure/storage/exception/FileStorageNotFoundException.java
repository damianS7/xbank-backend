package com.damian.xBank.infrastructure.storage.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class FileStorageNotFoundException extends FileStorageException {

    public FileStorageNotFoundException(String path, String fileName) {
        super(Exceptions.STORAGE_FILE_NOT_FOUND, path, fileName);
    }

}
