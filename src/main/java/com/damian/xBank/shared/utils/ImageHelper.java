package com.damian.xBank.shared.utils;

import com.damian.xBank.infrastructure.storage.exception.ImageTypeNotSupportedException;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ImageHelper {
    public static String getContentType(Resource resource) {
        try {
            return ImageHelper.getContentType(resource.getFile());
        } catch (IOException e) {
            throw new ImageTypeNotSupportedException(resource.getFilename(), "");
        }
    }

    public static String getContentType(File file) {
        String contentType;
        try {
            contentType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            throw new ImageTypeNotSupportedException(file.getName(), "");
        }

        if (contentType == null) {
            contentType = "image/jpg";
        }

        return contentType;
    }
}
