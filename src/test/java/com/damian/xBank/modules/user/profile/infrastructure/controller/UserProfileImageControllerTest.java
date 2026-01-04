package com.damian.xBank.modules.user.profile.infrastructure.controller;

import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserAccountRole;
import com.damian.xBank.modules.user.user.domain.model.UserAccountStatus;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.shared.infrastructure.storage.FileStorageService;
import com.damian.xBank.shared.infrastructure.storage.ImageUploaderService;
import com.damian.xBank.shared.infrastructure.storage.exception.FileStorageNotFoundException;
import com.damian.xBank.shared.utils.ImageTestHelper;
import com.damian.xBank.shared.utils.UserProfileTestFactory;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserProfileImageControllerTest extends AbstractControllerTest {
    private User customer;

    @MockitoBean
    private FileStorageService fileStorageService;

    @MockitoBean
    private ImageUploaderService imageUploaderService;

    @BeforeEach
    void setUp() {
        UserProfile profile = UserProfileTestFactory.aProfile();

        customer = UserTestBuilder
                .aCustomer()
                .withEmail("customer@demo.com")
                .withRole(UserAccountRole.CUSTOMER)
                .withStatus(UserAccountStatus.VERIFIED)
                .withPassword(RAW_PASSWORD)
                .withProfile(profile)
                .build();

        userAccountRepository.save(customer);
    }

    @Test
    @DisplayName("should return 200 and user profile image")
    void getProfileImage_WhenValidRequest_ReturnsProfileImage() throws Exception {
        // given
        login(customer);

        MultipartFile imageMultipart = ImageTestHelper.createDefaultJpg();
        File imageFile = ImageTestHelper.multipartToFile(imageMultipart);
        Resource imageResource = new UrlResource(imageFile.toURI());

        when(fileStorageService.getFile(anyString(), anyString())).thenReturn(imageFile);
        when(fileStorageService.createResource(any(File.class))).thenReturn(imageResource);

        mockMvc
                .perform(
                        get("/api/v1/profiles/{id}/image", customer.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.IMAGE_JPEG))
                .andReturn();
    }

    @Test
    @DisplayName("should return 404 when image not found")
    void getProfileImage_WhenImageNotFound_Returns404NotFound() throws Exception {
        // given
        login(customer);

        when(fileStorageService.getFile(anyString(), anyString())).thenThrow(
                new FileStorageNotFoundException("/path/", "image.jpg")
        );

        mockMvc
                .perform(
                        get("/api/v1/profiles/{id}/image", customer.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("should return 404 when user not exist")
    void getProfileImage_WhenUserNotExists_Returns404NotFound() throws Exception {
        // given
        login(customer);

        mockMvc
                .perform(
                        get("/api/v1/profiles/{id}/image", 99L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("should return 201 when successfully upload profile image")
    void postImage_WhenValidRequest_Returns201Created() throws Exception {
        // given
        login(customer);

        MockMultipartFile imageMultipart = ImageTestHelper.createDefaultJpg();
        File imageFile = ImageTestHelper.multipartToFile(imageMultipart);
        Resource imageResource = new UrlResource(imageFile.toURI());

        when(fileStorageService.getFile(anyString(), anyString())).thenReturn(imageFile);
        when(fileStorageService.createResource(any(File.class))).thenReturn(imageResource);

        when(imageUploaderService.uploadImage(
                any(MultipartFile.class),
                anyLong(),
                anyString(),
                anyString()
        )).thenReturn(imageFile);

        // when
        MvcResult result = mockMvc
                .perform(
                        multipart("/api/v1/profiles/image")
                                .file(imageMultipart)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .param("currentPassword", this.RAW_PASSWORD)
                                .with(request -> {
                                    request.setMethod("POST");
                                    return request;
                                }))

                .andDo(print())
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andReturn();

        byte[] content = result.getResponse().getContentAsByteArray();
        Resource resource = new ByteArrayResource(content);

        // then
        assertThat(resource).isNotNull();
        assertEquals(resource.contentLength(), imageMultipart.getBytes().length);
        assertEquals(result.getResponse().getContentType(), imageMultipart.getContentType());
    }

    @Test
    @DisplayName("should return 400 when uploaded image is empty")
    void postImage_WhenImageEmpty_Returns400BadRequest() throws Exception {
        // given
        login(customer);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                customer.getProfile().getPhotoPath(),
                "image/jpeg",
                new byte[0]
        );

        // when
        mockMvc
                .perform(
                        multipart("/api/v1/profiles/image")
                                .file(file)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .param("currentPassword", this.RAW_PASSWORD)
                                .with(request -> {
                                    request.setMethod("POST");
                                    return request;
                                }))

                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("should return 413 when uploaded image size exceeds limit")
    void postImage_WhenImageSizeExceedsLimit_Returns413PayloadTooLarge() throws Exception {
        // given
        login(customer);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                customer.getProfile().getPhotoPath(),
                "image/jpeg",
                new byte[5 * 1024 * 1024 + 1]
        );

        // when
        mockMvc
                .perform(
                        multipart("/api/v1/profiles/image")
                                .file(file)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .param("currentPassword", this.RAW_PASSWORD)
                                .with(request -> {
                                    request.setMethod("POST");
                                    return request;
                                }))
                .andDo(print())
                .andExpect(status().is(HttpStatus.PAYLOAD_TOO_LARGE.value()));
    }

    @Test
    @DisplayName("should return 415 when uploaded image type is not supported")
    void postImage_WhenImageTypeIsNotSupported_Returns415UnsupportedMediaType() throws Exception {
        // given
        login(customer);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                customer.getProfile().getPhotoPath(),
                "text/plain",
                new byte[5]
        );

        // when
        mockMvc
                .perform(
                        multipart("/api/v1/profiles/image")
                                .file(file)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .param("currentPassword", this.RAW_PASSWORD)
                                .with(request -> {
                                    request.setMethod("POST");
                                    return request;
                                }))

                .andDo(print())
                .andExpect(status().is(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()));
    }
}
