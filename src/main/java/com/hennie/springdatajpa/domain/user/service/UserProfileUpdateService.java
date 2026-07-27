package com.hennie.springdatajpa.domain.user.service;

import com.hennie.springdatajpa.domain.image.dto.response.ImageUploadResponseDto;
import com.hennie.springdatajpa.domain.image.service.LocalImageService;
import com.hennie.springdatajpa.domain.user.dto.request.UserInfoRequestDto;
import com.hennie.springdatajpa.domain.user.dto.response.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserProfileUpdateService {

    private final UserService userService;
    private final LocalImageService localImageService;

    public UserResponseDto update(
            Long userId,
            UserInfoRequestDto request,
            MultipartFile profileImage
    ) {
        if (profileImage == null) {
            return userService.updateUser(userId, request, null);
        }

        String previousProfileUrl = userService.getUser(userId).getProfileUrl();
        String uploadedProfileUrl = null;

        try {
            ImageUploadResponseDto uploadedImage = localImageService.upload(profileImage);
            uploadedProfileUrl = uploadedImage.getImageUrl();

            UserResponseDto updatedUser = userService.updateUser(
                    userId,
                    request,
                    uploadedProfileUrl
            );
            localImageService.deleteByUrl(previousProfileUrl);
            return updatedUser;
        } catch (RuntimeException exception) {
            if (uploadedProfileUrl != null) {
                localImageService.deleteByUrl(uploadedProfileUrl);
            }
            throw exception;
        }
    }
}
