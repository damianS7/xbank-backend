package com.damian.xBank.infrastructure.storage.exception;

import com.damian.xBank.shared.exception.ApplicationException;
import com.damian.xBank.shared.exception.ErrorCodes;

public class ImageCompressionFailedException extends ApplicationException {
    public ImageCompressionFailedException() {
        super(ErrorCodes.STORAGE_IMAGE_FAILED_COMPRESS, "", new Object[]{});
    }
}
