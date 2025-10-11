package com.damian.xBank.shared.utils;

import com.damian.xBank.shared.exception.Exceptions;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ImageHelper {
    public static String getContentType(Resource resource) {
        try {
            return ImageHelper.getContentType(resource.getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getContentType(File file) {
        String contentType;
        try {
            contentType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(Exceptions.IMAGE.TYPE_NOT_DETECTED, e);
        }

        if (contentType == null) {
            contentType = "image/jpg";
        }

        return contentType;
    }
}
