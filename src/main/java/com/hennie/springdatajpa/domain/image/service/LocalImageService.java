package com.hennie.springdatajpa.domain.image.service;

import com.hennie.springdatajpa.domain.image.config.ImageProperties;
import com.hennie.springdatajpa.domain.image.dto.response.ImageUploadResponseDto;
import com.hennie.springdatajpa.global.exception.BadRequestException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalImageService {

    private static final Map<String, String> ALLOWED_IMAGE_TYPES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp",
            "image/gif", "gif"
    );

    private final ImageProperties imageProperties;
    private Path uploadDirectory;

    @PostConstruct
    void initializeUploadDirectory() {
        uploadDirectory = Path.of(imageProperties.getUploadDirectory())
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(uploadDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException("IMAGE_DIRECTORY_INITIALIZATION_FAILED", exception);
        }
    }

    public ImageUploadResponseDto upload(MultipartFile file) {
        String extension = validateAndGetExtension(file);
        String storedFileName = UUID.randomUUID() + "." + extension;
        Path target = uploadDirectory.resolve(storedFileName).normalize();

        if (!target.getParent().equals(uploadDirectory)) {
            throw new BadRequestException("INVALID_IMAGE_FILE");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, target);
        } catch (IOException exception) {
            tryDelete(target);
            throw new IllegalStateException("IMAGE_UPLOAD_FAILED", exception);
        }

        return new ImageUploadResponseDto(buildImageUrl(storedFileName));
    }

    private String validateAndGetExtension(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("EMPTY_IMAGE_FILE");
        }

        String extension = ALLOWED_IMAGE_TYPES.get(file.getContentType());
        if (extension == null) {
            throw new BadRequestException("UNSUPPORTED_IMAGE_TYPE");
        }

        return extension;
    }

    private String buildImageUrl(String storedFileName) {
        String baseUrl = imageProperties.getBaseUrl();
        return baseUrl.endsWith("/")
                ? baseUrl + storedFileName
                : baseUrl + "/" + storedFileName;
    }

    private void tryDelete(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // 업로드 실패 원인을 유지하기 위해 정리 실패는 별도로 전파하지 않는다.
        }
    }
}
