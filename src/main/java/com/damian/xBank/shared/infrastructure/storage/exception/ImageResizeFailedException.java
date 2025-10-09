package com.damian.whatsapp.shared.infrastructure.storage.exception;

import com.damian.whatsapp.shared.exception.ApplicationException;

public class ImageResizeFailedException extends ApplicationException {
    public ImageResizeFailedException(String message) {
        super(message);
    }
}
