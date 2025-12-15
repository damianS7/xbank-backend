package com.damian.xBank.infrastructure.storage.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class ImageResizeFailedException extends ApplicationException {
    public ImageResizeFailedException(String message) {
        super(message);
    }
}
