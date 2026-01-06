package com.damian.xBank.shared.infrastructure.storage.exception;

import com.damian.xBank.shared.exception.ErrorCodes;

public class FileStorageDeleteException extends FileStorageException {

    public FileStorageDeleteException(String path, String filename) {
        super(ErrorCodes.STORAGE_FILE_DELETE_FAILED, path, filename);
    }

}
