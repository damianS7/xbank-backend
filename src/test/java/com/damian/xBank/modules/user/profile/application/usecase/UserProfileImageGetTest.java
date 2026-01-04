package com.damian.xBank.modules.user.profile.application.usecase;

import com.damian.xBank.modules.user.profile.domain.exception.UserProfileImageNotFoundException;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.profile.infrastructure.service.UserProfileImageService;
import com.damian.xBank.modules.user.user.domain.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserAccountRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
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

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UserProfileImageGetTest extends AbstractServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private UserProfileImageService userProfileImageService;

    @InjectMocks
    private UserProfileImageGet userProfileImageGet;
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
    @DisplayName("should return user image")
    void getImage_WhenValidRequest_ReturnsImage() throws IOException {
        // given
        File givenFile = ImageTestHelper.multipartToFile(
                ImageTestHelper.createDefaultJpg()
        );

        Resource givenResource = new UrlResource(givenFile.toURI());

        // when
        when(userAccountRepository.findById(customer.getId()))
                .thenReturn(Optional.of(customer));


        when(userProfileImageService.getImage(
                customer.getId(),
                customer.getProfile().getPhotoPath()
        )).thenReturn(givenResource);

        Resource resource = userProfileImageGet.execute(customer.getId());

        // then
        assertNotNull(resource);
        assertTrue(resource.exists());
        assertEquals(givenFile.length(), resource.getFile().length());
    }

    @Test
    @DisplayName("should throw exception when user not found")
    void getImage_WhenUserNotFound_ThrowsException() throws IOException {
        // given
        // when
        when(userAccountRepository.findById(customer.getId())).thenReturn(Optional.empty());

        UserAccountNotFoundException exception = assertThrows(
                UserAccountNotFoundException.class,
                () -> userProfileImageGet.execute(customer.getId())
        );

        // then
        assertNotNull(exception);
        assertEquals(ErrorCodes.USER_ACCOUNT_NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("should throw exception when user image is null")
    void getImage_WhenUserImageIsNull_ThrowsException() throws IOException {
        // given
        customer.getProfile().setPhotoPath(null);

        // when
        when(userAccountRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        UserProfileImageNotFoundException exception = assertThrows(
                UserProfileImageNotFoundException.class,
                () -> userProfileImageGet.execute(customer.getId())
        );

        // then
        assertNotNull(exception);
        assertEquals(ErrorCodes.PROFILE_IMAGE_NOT_FOUND, exception.getMessage());
    }
}
