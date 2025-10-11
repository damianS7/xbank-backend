package com.damian.xBank.shared.infrastructure.storage.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class ImageCompressionFailedException extends ApplicationException {
    public ImageCompressionFailedException(String message) {
        super(message);
    }
}
