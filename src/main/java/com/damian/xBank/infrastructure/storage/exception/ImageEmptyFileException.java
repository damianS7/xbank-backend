package com.damian.xBank.infrastructure.storage.exception;

import com.damian.xBank.shared.exception.ApplicationException;
import com.damian.xBank.shared.exception.Exceptions;

public class ImageEmptyFileException extends ApplicationException {
    public ImageEmptyFileException(String filename) {
        super(Exceptions.STORAGE_IMAGE_EMPTY, filename, new Object[]{});
    }
}
