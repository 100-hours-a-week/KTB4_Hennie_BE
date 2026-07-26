package com.hennie.springdatajpa.domain.user.service;

import com.hennie.springdatajpa.domain.image.dto.response.ImageUploadResponseDto;
import com.hennie.springdatajpa.domain.image.service.LocalImageService;
import com.hennie.springdatajpa.domain.user.dto.request.UserRequestDto;
import com.hennie.springdatajpa.domain.user.dto.response.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserSignupService {

    private final UserService userService;
    private final LocalImageService localImageService;

    public UserResponseDto signup(UserRequestDto request, MultipartFile profileImage) {
        String profileUrl = null;

        try {
            if (profileImage != null) {
                ImageUploadResponseDto uploadedImage = localImageService.upload(profileImage);
                profileUrl = uploadedImage.getImageUrl();
            }

            return userService.createUser(request, profileUrl);
        } catch (RuntimeException exception) {
            if (profileUrl != null) {
                localImageService.deleteByUrl(profileUrl);
            }
            throw exception;
        }
    }
}
