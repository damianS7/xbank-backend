package com.damian.xBank.shared.infrastructure.storage.exception;

import com.damian.xBank.shared.domain.exception.ApplicationException;
import com.damian.xBank.shared.domain.exception.ErrorCodes;

public class ImageNotFoundException extends ApplicationException {
    public ImageNotFoundException(String filename, String path) {
        super(ErrorCodes.STORAGE_IMAGE_NOT_FOUND, filename, new Object[]{path});
    }
}
