package com.damian.xBank.infrastructure.storage.exception;

import com.damian.xBank.shared.exception.ApplicationException;
import com.damian.xBank.shared.exception.Exceptions;

public class ImageTypeNotSupportedException extends ApplicationException {

    public ImageTypeNotSupportedException(String filename, String type) {
        super(Exceptions.STORAGE_IMAGE_INVALID_TYPE, filename, new Object[]{type});
    }
}
