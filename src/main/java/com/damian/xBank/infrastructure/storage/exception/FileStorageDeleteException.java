package com.damian.xBank.infrastructure.storage.exception;

import com.damian.xBank.shared.exception.Exceptions;

public class FileStorageDeleteException extends FileStorageException {

    public FileStorageDeleteException(String path, String filename) {
        super(Exceptions.STORAGE_FILE_DELETE_FAILED, path, filename);
    }

}
