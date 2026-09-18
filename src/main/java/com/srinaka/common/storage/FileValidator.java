package com.srinaka.common.storage;

import com.srinaka.common.error.BusinessException;
import com.srinaka.common.error.ErrorCode;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public final class FileValidator {

    private static final long MAX_IMAGE_BYTES = 5L * 1024 * 1024;

    private FileValidator() {
    }

    public static void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.EMPTY_FILE);
        }
        if (file.getSize() > MAX_IMAGE_BYTES) {
            throw new BusinessException(ErrorCode.FILE_TOO_LARGE);
        }
        if (detectImageFormat(file) == null) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    private static String detectImageFormat(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            byte[] header = in.readNBytes(12);

            if (header.length >= 3
                    && (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF) {
                return "jpg";
            }
            if (header.length >= 8
                    && (header[0] & 0xFF) == 0x89 && header[1] == 'P' && header[2] == 'N' && header[3] == 'G') {
                return "png";
            }
            if (header.length >= 12
                    && header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
                    && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P') {
                return "webp";
            }
            return null;
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }
}
