package com.hennie.springdatajpa.domain.image.controller;

import com.hennie.springdatajpa.domain.image.dto.response.ImageUploadResponseDto;
import com.hennie.springdatajpa.domain.image.service.LocalImageService;
import com.hennie.springdatajpa.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final LocalImageService localImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImageUploadResponseDto>> uploadImage(
            @RequestPart("file") MultipartFile file
    ) {
        ImageUploadResponseDto result = localImageService.upload(file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("IMAGE_UPLOAD_SUCCESS", result));
    }
}
