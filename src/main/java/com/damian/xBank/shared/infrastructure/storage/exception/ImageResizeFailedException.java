package com.damian.xBank.shared.infrastructure.storage.exception;

import com.damian.xBank.shared.exception.ApplicationException;
import com.damian.xBank.shared.exception.ErrorCodes;

public class ImageResizeFailedException extends ApplicationException {
    public ImageResizeFailedException() {
        super(ErrorCodes.STORAGE_IMAGE_FAILED_RESIZE, "", new Object[]{});
    }
}
