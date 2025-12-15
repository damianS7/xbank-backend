package com.damian.xBank.infrastructure.storage.exception;

import com.damian.xBank.shared.exception.ApplicationException;

public class ImageTooLargeException extends ApplicationException {
    private final String size;

    public ImageTooLargeException(String message, String size) {
        super(message);
        this.size = size;
    }

    public ImageTooLargeException(String message, Long size) {
        this(message, size.toString());
    }

    public ImageTooLargeException(String message) {
        super(message);
        this.size = null;
    }

    public String getImageSize() {
        return size;
    }
}
