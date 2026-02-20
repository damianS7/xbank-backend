package com.damian.xBank.shared.infrastructure.storage.exception;

import com.damian.xBank.shared.domain.exception.ApplicationException;
import com.damian.xBank.shared.domain.exception.ErrorCodes;

public class ImageCompressionFailedException extends ApplicationException {
    public ImageCompressionFailedException() {
        super(ErrorCodes.STORAGE_IMAGE_FAILED_COMPRESS, "", new Object[]{});
    }
}
