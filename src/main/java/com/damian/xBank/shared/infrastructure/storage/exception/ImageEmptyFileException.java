package com.damian.whatsapp.shared.infrastructure.storage.exception;

import com.damian.whatsapp.shared.exception.ApplicationException;

public class ImageEmptyFileException extends ApplicationException {
    public ImageEmptyFileException(String message) {
        super(message);
    }
}
