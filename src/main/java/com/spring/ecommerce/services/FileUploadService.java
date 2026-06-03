package com.spring.ecommerce.services;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private final Cloudinary cloudinary;

    public String upload(String objectType, UUID objectId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }

        if (objectId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Object id is required");
        }

        String normalizedObjectType = normalizeObjectType(objectType);
        getValidatedExtension(file);
        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of(
                "folder", normalizedObjectType,
                "public_id", normalizedObjectType + "-" + objectId,
                "overwrite", true,
                "resource_type", "image"
        ));

        Object secureUrl = uploadResult == null ? null : uploadResult.get("secure_url");
        if (secureUrl == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Image upload failed");
        }
        return secureUrl.toString();
    }

    private String normalizeObjectType(String objectType) {
        if (objectType == null || objectType.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Object type is required");
        }

        return objectType.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9-]", "-");
    }

    private String getValidatedExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank() || !originalFilename.contains(".")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File name is invalid");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only .jpg, .jpeg, .png and .webp files are allowed");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only .jpg, .jpeg, .png and .webp files are allowed");
        }

        return extension;
    }
}
