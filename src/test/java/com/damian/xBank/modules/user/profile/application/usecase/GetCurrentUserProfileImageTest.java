package com.damian.xBank.modules.user.profile.application.usecase;

import com.damian.xBank.modules.user.profile.application.usecase.get.GetCurrentUserProfileImage;
import com.damian.xBank.modules.user.profile.application.usecase.get.GetUserProfileImageQuery;
import com.damian.xBank.modules.user.profile.domain.exception.UserProfileImageNotFoundException;
import com.damian.xBank.modules.user.profile.infrastructure.service.UserProfileImageService;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.ImageTestHelper;
import com.damian.xBank.test.utils.UserTestFactory;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class GetCurrentUserProfileImageTest extends AbstractServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileImageService userProfileImageService;

    @InjectMocks
    private GetCurrentUserProfileImage getCurrentUserProfileImage;
    private User customer;

    @BeforeEach
    void setUp() {
        customer = UserTestFactory.aCustomerWithId(1L);
    }

    @Test
    @DisplayName("should return user image")
    void getImage_WhenValidRequest_ReturnsImage() throws IOException {
        // given
        File givenFile = ImageTestHelper.multipartToFile(
            ImageTestHelper.createDefaultJpg()
        );

        Resource givenResource = new UrlResource(givenFile.toURI());

        GetUserProfileImageQuery query = new GetUserProfileImageQuery(customer.getId());

        // when
        when(userRepository.findById(customer.getId()))
            .thenReturn(Optional.of(customer));


        when(userProfileImageService.getImage(
            customer.getId(),
            customer.getProfile().getPhotoPath()
        )).thenReturn(givenResource);

        Resource resource = getCurrentUserProfileImage.execute(query);

        // then
        assertNotNull(resource);
        assertTrue(resource.exists());
        assertEquals(givenFile.length(), resource.getFile().length());
    }

    @Test
    @DisplayName("should throw exception when user not found")
    void getImage_WhenUserNotFound_ThrowsException() throws IOException {
        // given
        GetUserProfileImageQuery query = new GetUserProfileImageQuery(customer.getId());
        // when
        when(userRepository.findById(customer.getId())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> getCurrentUserProfileImage.execute(query)
        );

        // then
        assertNotNull(exception);
        assertEquals(ErrorCodes.USER_NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("should throw exception when user image is null")
    void getImage_WhenUserImageIsNull_ThrowsException() throws IOException {
        // given
        customer.getProfile().setPhotoPath(null);
        GetUserProfileImageQuery query = new GetUserProfileImageQuery(customer.getId());

        // when
        when(userRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        UserProfileImageNotFoundException exception = assertThrows(
            UserProfileImageNotFoundException.class,
            () -> getCurrentUserProfileImage.execute(query)
        );

        // then
        assertNotNull(exception);
        assertEquals(ErrorCodes.PROFILE_IMAGE_NOT_FOUND, exception.getMessage());
    }
}
