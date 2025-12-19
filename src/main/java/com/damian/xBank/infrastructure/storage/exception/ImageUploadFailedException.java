package com.damian.xBank.infrastructure.storage.exception;

import com.damian.xBank.shared.exception.ApplicationException;
import com.damian.xBank.shared.exception.ErrorCodes;

public class ImageUploadFailedException extends ApplicationException {
    public ImageUploadFailedException(String path) {
        super(ErrorCodes.STORAGE_IMAGE_UPLOAD_FAILED, path, new Object[]{});
    }
}
