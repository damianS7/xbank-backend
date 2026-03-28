package com.damian.xBank.modules.user.profile.application.usecase;

import com.damian.xBank.modules.user.profile.application.usecase.update.UploadUserProfileImage;
import com.damian.xBank.modules.user.profile.application.usecase.update.UploadUserProfileImageCommand;
import com.damian.xBank.modules.user.profile.infrastructure.service.UserProfileImageService;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.ImageTestHelper;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UploadUserProfileImageTest extends AbstractServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileImageService userProfileImageService;

    @InjectMocks
    private UploadUserProfileImage uploadUserProfileImage;

    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.aCustomer()
            .withId(1L)
            .build();
    }

    @Test
    @DisplayName("should return image file after upload")
    void uploadImage_WhenValidRequest_ReturnsImageFile() {
        // given
        setUpContext(customer);
        MultipartFile givenMultipart = ImageTestHelper.createDefaultJpg();
        File tempFile = ImageTestHelper.multipartToFile(givenMultipart);

        UploadUserProfileImageCommand command = new UploadUserProfileImageCommand(
            RAW_PASSWORD, givenMultipart
        );

        // when
        when(userRepository.save(any(User.class)))
            .thenReturn(customer);

        when(userProfileImageService.uploadImage(
            anyLong(),
            any(MultipartFile.class)
        )).thenReturn(tempFile);

        File resultImage = uploadUserProfileImage.execute(command);

        // then
        assertNotNull(resultImage);
        assertEquals(resultImage.length(), tempFile.length());
        verify(userRepository, times(1)).save(any(User.class));
    }
}
