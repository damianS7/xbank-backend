package com.damian.xBank.modules.user.customer;

import com.damian.xBank.modules.user.account.account.enums.UserAccountRole;
import com.damian.xBank.modules.user.account.account.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.account.account.repository.UserAccountRepository;
import com.damian.xBank.modules.user.customer.exception.CustomerImageNotFoundException;
import com.damian.xBank.modules.user.customer.service.CustomerImageService;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.domain.Customer;
import com.damian.xBank.shared.domain.UserAccount;
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.infrastructure.storage.FileStorageService;
import com.damian.xBank.shared.infrastructure.storage.ImageProcessingService;
import com.damian.xBank.shared.infrastructure.storage.ImageUploaderService;
import com.damian.xBank.shared.infrastructure.storage.ImageValidationService;
import com.damian.xBank.shared.utils.ImageTestHelper;
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

public class CustomerImageServiceTest extends AbstractServiceTest {

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
    private CustomerImageService customerImageService;
    private Customer customer;

    @BeforeEach
    void setUp() {
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("user@test.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD))
                                             .setRole(UserAccountRole.CUSTOMER);

        customer = Customer.create(userAccount)
                           .setPhotoPath("avatar.jpg");
    }

    @Test
    @DisplayName("Should get customer image")
    void shouldGetUserImage() throws IOException {
        // given
        File givenFile = ImageTestHelper.multipartToFile(
                ImageTestHelper.createDefaultJpg()
        );

        Resource givenResource = new UrlResource(givenFile.toURI());

        // when
        when(userAccountRepository.findById(customer
                .getAccount()
                .getId())).thenReturn(Optional.of(customer.getAccount()));
        when(fileStorageService.getFile(anyString(), anyString())).thenReturn(givenFile);
        when(fileStorageService.createResource(givenFile)).thenReturn(givenResource);
        Resource resource = customerImageService.getUserImage(customer.getAccount().getId());

        // then
        assertNotNull(resource);
        assertTrue(resource.exists());
        assertEquals(givenFile.length(), resource.getFile().length());
    }

    @Test
    @DisplayName("Should not get user image when user not found")
    void shouldNotGetUserImageWhenUserNotFound() throws IOException {
        // given
        UserAccount userAccount = customer.getAccount();

        // when
        when(userAccountRepository.findById(userAccount.getId())).thenReturn(Optional.empty());

        UserAccountNotFoundException exception = assertThrows(
                UserAccountNotFoundException.class,
                () -> customerImageService.getUserImage(userAccount.getId())
        );

        // then
        assertNotNull(exception);
        assertEquals(Exceptions.CUSTOMER.IMAGE.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should not get user image when user image is null")
    void shouldNotGetUserImageWhenUserImageIsNull() throws IOException {
        // given
        UserAccount userAccount = customer.getAccount();
        userAccount.getCustomer().setPhotoPath(null);

        // when
        when(userAccountRepository.findById(userAccount.getId())).thenReturn(Optional.of(userAccount));

        CustomerImageNotFoundException exception = assertThrows(
                CustomerImageNotFoundException.class,
                () -> customerImageService.getUserImage(userAccount.getId())
        );

        // then
        assertNotNull(exception);
        assertEquals(Exceptions.CUSTOMER.IMAGE.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should upload user image")
    void shouldUploadUserImage() {
        // given
        UserAccount userAccount = customer.getAccount();
        setUpContext(userAccount);
        MultipartFile givenMultipart = ImageTestHelper.createDefaultJpg();
        File tempFile = ImageTestHelper.multipartToFile(givenMultipart);

        // when
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(userAccount);
        doNothing().when(imageValidationService).validateImage(any(), any(Long.class), any(String[].class));
        when(imageProcessingService.optimizeImage(any(), any(Integer.class), any(Integer.class))).thenReturn(
                givenMultipart);
        when(imageUploaderService.uploadImage(
                any(MultipartFile.class),
                anyString(),
                anyString()
        )).thenReturn(tempFile);

        File uploadedImage = customerImageService.uploadUserImage(
                RAW_PASSWORD, givenMultipart
        );

        // then
        assertNotNull(uploadedImage);
        assertEquals(uploadedImage.length(), tempFile.length());
        verify(userAccountRepository, times(1)).save(any(UserAccount.class));
    }
}
