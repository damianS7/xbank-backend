package com.damian.xBank.modules.user.profile.infrastructure.service;

import com.damian.xBank.modules.user.profile.domain.exception.UserProfileImageNotFoundException;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.user.domain.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserAccountRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.infrastructure.storage.FileStorageService;
import com.damian.xBank.shared.infrastructure.storage.ImageProcessingService;
import com.damian.xBank.shared.infrastructure.storage.ImageUploaderService;
import com.damian.xBank.shared.infrastructure.storage.ImageValidationService;
import com.damian.xBank.shared.utils.ImageTestHelper;
import com.damian.xBank.shared.utils.UserProfileTestFactory;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserProfileImageServiceTest extends AbstractServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private ImageUploaderService imageUploaderService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ImageValidationService imageValidationService;

    @Mock
    private ImageProcessingService imageProcessingService;

    @InjectMocks
    private UserProfileImageService userProfileImageService;
    private User customer;

    @BeforeEach
    void setUp() {
        UserProfile profile = UserProfileTestFactory.aProfile();

        customer = UserTestBuilder.aCustomer()
                                  .withId(1L)
                                  .withPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                                  .withEmail("customer@demo.com")
                                  .withProfile(profile)
                                  .build();
    }

    @Test
    @DisplayName("Should get customer image")
    void shouldGetImage() throws IOException {
        // given
        File givenFile = ImageTestHelper.multipartToFile(
                ImageTestHelper.createDefaultJpg()
        );

        Resource givenResource = new UrlResource(givenFile.toURI());

        // when
        when(userAccountRepository.findById(customer.getId()))
                .thenReturn(Optional.of(customer));
        when(fileStorageService.getFile(anyString(), anyString())).thenReturn(givenFile);
        when(fileStorageService.createResource(givenFile)).thenReturn(givenResource);
        Resource resource = userProfileImageService.getImage(customer.getId());

        // then
        assertNotNull(resource);
        assertTrue(resource.exists());
        assertEquals(givenFile.length(), resource.getFile().length());
    }

    @Test
    @DisplayName("Should not get user image when user not found")
    void shouldNotGetUserImageWhenNotFound() throws IOException {
        // given
        // when
        when(userAccountRepository.findById(customer.getId())).thenReturn(Optional.empty());

        UserAccountNotFoundException exception = assertThrows(
                UserAccountNotFoundException.class,
                () -> userProfileImageService.getImage(customer.getId())
        );

        // then
        assertNotNull(exception);
        assertEquals(ErrorCodes.USER_ACCOUNT_NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should not get user image when user image is null")
    void shouldNotGetUserImageWhenImageIsNull() throws IOException {
        // given
        customer.getProfile().setPhotoPath(null);

        // when
        when(userAccountRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        UserProfileImageNotFoundException exception = assertThrows(
                UserProfileImageNotFoundException.class,
                () -> userProfileImageService.getImage(customer.getId())
        );

        // then
        assertNotNull(exception);
        assertEquals(ErrorCodes.PROFILE_IMAGE_NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should upload user image")
    void shouldUploadImage() {
        // given
        setUpContext(customer);
        MultipartFile givenMultipart = ImageTestHelper.createDefaultJpg();
        File tempFile = ImageTestHelper.multipartToFile(givenMultipart);

        // when
        when(userAccountRepository.save(any(User.class))).thenReturn(customer);
        doNothing().when(imageValidationService).validateImage(any(), any(Long.class), any(String[].class));
        when(imageProcessingService.optimizeImage(any(), any(Integer.class), any(Integer.class))).thenReturn(
                givenMultipart);
        when(imageUploaderService.uploadImage(
                any(MultipartFile.class),
                anyString(),
                anyString()
        )).thenReturn(tempFile);

        File uploadedImage = userProfileImageService.uploadImage(
                RAW_PASSWORD, givenMultipart
        );

        // then
        assertNotNull(uploadedImage);
        assertEquals(uploadedImage.length(), tempFile.length());
        verify(userAccountRepository, times(1)).save(any(User.class));
    }
}
