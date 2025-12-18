package com.damian.xBank.infrastructure.storage.exception;

import com.damian.xBank.shared.exception.ApplicationException;
import com.damian.xBank.shared.exception.Exceptions;

public class ImageNotFoundException extends ApplicationException {
    public ImageNotFoundException(String filename, String path) {
        super(Exceptions.STORAGE_IMAGE_NOT_FOUND, filename, new Object[]{path});
    }
}
