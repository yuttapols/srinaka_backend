package com.srinaka.common.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    StoredFile upload(MultipartFile file, String folder, boolean authenticated);

    void delete(String publicId, String resourceType, boolean authenticated);

    String generateSignedUrl(String publicId, String resourceType);
}
