package com.damian.xBank.modules.user.profile.application.usecase;

import com.damian.xBank.modules.user.profile.domain.factory.UserProfileFactory;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.profile.infrastructure.repository.UserProfileRepository;
import com.damian.xBank.modules.user.profile.infrastructure.service.UserProfileImageService;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.ImageTestHelper;
import com.damian.xBank.shared.utils.UserTestBuilder;
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
import static org.mockito.Mockito.*;

public class UserProfileImageUploadTest extends AbstractServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserProfileImageService userProfileImageService;

    @InjectMocks
    private UserProfileImageUpload userProfileImageUpload;

    private User customer;

    @BeforeEach
    void setUp() {
        UserProfile profile = UserProfileFactory.testProfile();

        customer = UserTestBuilder.aCustomer()
                                  .withId(1L)
                                  .withPassword(RAW_PASSWORD)
                                  .withEmail("customer@demo.com")
                                  .withProfile(profile)
                                  .build();
    }

    @Test
    @DisplayName("should return image file after upload")
    void uploadImage_WhenValidRequest_ReturnsImageFile() {
        // given
        setUpContext(customer);
        MultipartFile givenMultipart = ImageTestHelper.createDefaultJpg();
        File tempFile = ImageTestHelper.multipartToFile(givenMultipart);

        // when
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(customer.getProfile());

        when(userProfileImageService.uploadImage(
                anyLong(),
                any(MultipartFile.class)
        )).thenReturn(tempFile);

        File resultImage = userProfileImageUpload.execute(
                RAW_PASSWORD, givenMultipart
        );

        // then
        assertNotNull(resultImage);
        assertEquals(resultImage.length(), tempFile.length());
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }
}
