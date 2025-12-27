package com.damian.xBank.shared.infrastructure.storage.exception;

import com.damian.xBank.shared.exception.ApplicationException;
import com.damian.xBank.shared.exception.ErrorCodes;

public class ImageTypeNotSupportedException extends ApplicationException {

    public ImageTypeNotSupportedException(String filename, String type) {
        super(ErrorCodes.STORAGE_IMAGE_INVALID_TYPE, filename, new Object[]{type});
    }
}
