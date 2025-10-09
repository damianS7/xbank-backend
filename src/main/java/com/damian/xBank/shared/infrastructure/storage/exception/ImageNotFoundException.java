package com.damian.whatsapp.shared.infrastructure.storage.exception;

import com.damian.whatsapp.shared.exception.ApplicationException;

public class ImageNotFoundException extends ApplicationException {
    private final String path;
    private final String imageName;

    public ImageNotFoundException(String message, String path, String imageName) {
        super(message);
        this.path = path;
        this.imageName = imageName;
    }

    public ImageNotFoundException(String message) {
        this(message, null, null);
    }

    public String getImageName() {
        return imageName;
    }

    public String getPath() {
        return path;
    }
}
