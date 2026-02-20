package com.damian.xBank.shared.infrastructure.storage.exception;

import com.damian.xBank.shared.domain.exception.ApplicationException;
import com.damian.xBank.shared.domain.exception.ErrorCodes;

public class ImageEmptyFileException extends ApplicationException {
    public ImageEmptyFileException(String filename) {
        super(ErrorCodes.STORAGE_IMAGE_EMPTY, filename, new Object[]{});
    }
}
