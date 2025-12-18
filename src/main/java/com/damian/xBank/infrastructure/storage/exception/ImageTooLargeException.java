package com.damian.xBank.infrastructure.storage.exception;

import com.damian.xBank.shared.exception.ApplicationException;
import com.damian.xBank.shared.exception.Exceptions;

public class ImageTooLargeException extends ApplicationException {

    public ImageTooLargeException(String filename, Object size) {
        super(Exceptions.STORAGE_UPLOAD_FILE_TOO_LARGE, filename, new Object[]{size});
    }
}
