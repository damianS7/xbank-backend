package com.damian.xBank.infrastructure.storage.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class ImageEmptyFileException extends ApplicationException {
    public ImageEmptyFileException(String message) {
        super(message);
    }
}
