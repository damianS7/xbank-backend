package com.damian.xBank.shared.infrastructure.storage.exception;

import com.damian.xBank.shared.domain.exception.ApplicationException;
import com.damian.xBank.shared.domain.exception.ErrorCodes;

public class ImageTooLargeException extends ApplicationException {

    public ImageTooLargeException(String filename, Object size) {
        super(ErrorCodes.STORAGE_IMAGE_UPLOAD_TOO_LARGE, filename, new Object[]{size});
    }
}
