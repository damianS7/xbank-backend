package com.damian.xBank.infrastructure.storage.exception;

import com.damian.xBank.shared.exception.ApplicationException;
import com.damian.xBank.shared.exception.ErrorCodes;

public class ImageEmptyFileException extends ApplicationException {
    public ImageEmptyFileException(String filename) {
        super(ErrorCodes.STORAGE_IMAGE_EMPTY, filename, new Object[]{});
    }
}
