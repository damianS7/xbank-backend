package com.damian.xBank.shared.infrastructure.storage.exception;

import com.damian.xBank.shared.domain.exception.ApplicationException;
import com.damian.xBank.shared.domain.exception.ErrorCodes;

public class ImageResizeFailedException extends ApplicationException {
    public ImageResizeFailedException() {
        super(ErrorCodes.STORAGE_IMAGE_FAILED_RESIZE, "", new Object[]{});
    }
}
