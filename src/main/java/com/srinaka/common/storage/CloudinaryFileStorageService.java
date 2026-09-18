package com.srinaka.common.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.srinaka.common.error.BusinessException;
import com.srinaka.common.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryFileStorageService implements FileStorageService {

    private final Cloudinary cloudinary;

    @Override
    public StoredFile upload(MultipartFile file, String folder, boolean authenticated) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "auto",
                    "type", authenticated ? "authenticated" : "upload"));

            return new StoredFile(
                    (String) result.get("public_id"),
                    (String) result.get("secure_url"),
                    file.getOriginalFilename(),
                    (String) result.get("format"),
                    (String) result.get("resource_type"),
                    ((Number) result.get("bytes")).longValue());
        } catch (IOException | RuntimeException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public void delete(String publicId, String resourceType, boolean authenticated) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap(
                    "resource_type", resourceType,
                    "type", authenticated ? "authenticated" : "upload"));
        } catch (IOException | RuntimeException e) {
            throw new BusinessException(ErrorCode.FILE_DELETE_FAILED);
        }
    }

    @Override
    public String generateSignedUrl(String publicId, String resourceType) {
        return cloudinary.url()
                .resourceType(resourceType)
                .type("authenticated")
                .signed(true)
                .secure(true)
                .generate(publicId);
    }
}
