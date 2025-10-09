package com.damian.whatsapp.shared.infrastructure.storage.exception;

import com.damian.whatsapp.shared.exception.ApplicationException;

public class ImageCompressionFailedException extends ApplicationException {
    public ImageCompressionFailedException(String message) {
        super(message);
    }
}
