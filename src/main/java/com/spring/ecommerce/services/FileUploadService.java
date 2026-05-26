package com.spring.ecommerce.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileUploadService {
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    public String upload(String objectType, UUID objectId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }

        if (objectId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Object id is required");
        }

        String normalizedObjectType = normalizeObjectType(objectType);
        String extension = getValidatedExtension(file);
        String fileName = normalizedObjectType + "-" + objectId + "-" + LocalDateTime.now() + "." + extension;
        Path uploadDirectory = Path.of("images", normalizedObjectType);
        Path filePath = uploadDirectory.resolve(fileName);

        Files.createDirectories(uploadDirectory);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only .jpg, .jpeg, and .png files are allowed");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only JPEG and PNG images are allowed");
        }

        return extension;
    }
}
