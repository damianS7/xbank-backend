package com.damian.xBank.modules.user.customer.infrastructure.controller;

import com.damian.xBank.modules.user.customer.application.dto.request.CustomerUpdateRequest;
import com.damian.xBank.modules.user.customer.application.dto.response.CustomerDetailDto;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.modules.user.customer.domain.enums.CustomerGender;
import com.damian.xBank.shared.AbstractControllerTest;
import com.damian.xBank.infrastructure.storage.FileStorageService;
import com.damian.xBank.infrastructure.storage.ImageUploaderService;
import com.damian.xBank.infrastructure.storage.exception.FileStorageNotFoundException;
import com.damian.xBank.shared.utils.ImageTestHelper;
import com.damian.xBank.shared.utils.JsonHelper;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CustomerControllerTest extends AbstractControllerTest {
    private Customer customer;

    @MockitoBean
    private FileStorageService fileStorageService;

    @MockitoBean
    private ImageUploaderService imageUploaderService;

    @BeforeEach
    void setUp() {
        customer = Customer.create()
                           .setEmail("customer@test.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
                           .setNationalId("123456789Z")
                           .setFirstName("John")
                           .setLastName("Wick")
                           .setGender(CustomerGender.MALE)
                           .setBirthdate(LocalDate.of(1989, 1, 1))
                           .setCountry("USA")
                           .setAddress("fake ave")
                           .setPostalCode("050012")
                           .setPhotoPath("no photoPath");
        customerRepository.save(customer);
    }

    @Test
    @DisplayName("Should get customer")
    void shouldGetCustomer() throws Exception {
        // given
        login(customer);

        // when
        MvcResult result = mockMvc
                .perform(
                        get("/api/v1/customers")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // then
        CustomerDetailDto customerWithProfileDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CustomerDetailDto.class
        );

        // then
        assertThat(customerWithProfileDTO).isNotNull();
        assertThat(customerWithProfileDTO.email()).isEqualTo(customer.getAccount().getEmail());
    }

    @Test
    @DisplayName("Should update customer")
    void shouldUpdateCustomer() throws Exception {
        // given
        login(customer);

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "alice");
        fields.put("lastName", "white");
        fields.put("phoneNumber", "999 999 999");
        fields.put("birthdate", LocalDate.of(1989, 1, 1));
        fields.put("gender", CustomerGender.FEMALE);

        CustomerUpdateRequest givenRequest = new CustomerUpdateRequest(
                RAW_PASSWORD,
                fields
        );

        // when
        MvcResult result = mockMvc
                .perform(
                        patch("/api/v1/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .content(JsonHelper.toJson(givenRequest)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // then
        CustomerDetailDto customerDto = JsonHelper.fromJson(
                result.getResponse().getContentAsString(),
                CustomerDetailDto.class
        );

        assertThat(customerDto)
                .isNotNull()
                .extracting(
                        CustomerDetailDto::firstName,
                        CustomerDetailDto::lastName,
                        CustomerDetailDto::phone,
                        CustomerDetailDto::birthdate,
                        CustomerDetailDto::gender
                ).containsExactly(
                        givenRequest.fieldsToUpdate().get("firstName"),
                        givenRequest.fieldsToUpdate().get("lastName"),
                        givenRequest.fieldsToUpdate().get("phoneNumber"),
                        givenRequest.fieldsToUpdate().get("birthdate"),
                        givenRequest.fieldsToUpdate().get("gender")
                );
    }

    @Test
    @DisplayName("Should get customer image")
    void shouldGetCustomerImage() throws Exception {
        // given
        login(customer);

        MultipartFile imageMultipart = ImageTestHelper.createDefaultJpg();
        File imageFile = ImageTestHelper.multipartToFile(imageMultipart);
        Resource imageResource = new UrlResource(imageFile.toURI());

        when(fileStorageService.getFile(anyString(), anyString())).thenReturn(imageFile);
        when(fileStorageService.createResource(any(File.class))).thenReturn(imageResource);

        mockMvc
                .perform(
                        get("/api/v1/customers/{id}/image", customer.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.IMAGE_JPEG))
                .andReturn();
    }

    @Test
    @DisplayName("Should not get customer image when not exist")
    void shouldNotGetCustomerImageWhenNotExist() throws Exception {
        // given
        login(customer);

        when(fileStorageService.getFile(anyString(), anyString())).thenThrow(
                FileStorageNotFoundException.class
        );

        mockMvc
                .perform(
                        get("/api/v1/customers/{id}/image", customer.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should upload customer image")
    void shouldUploadCustomerImage() throws Exception {
        // given
        login(customer);

        MockMultipartFile imageMultipart = ImageTestHelper.createDefaultJpg();
        File imageFile = ImageTestHelper.multipartToFile(imageMultipart);
        Resource imageResource = new UrlResource(imageFile.toURI());

        when(fileStorageService.getFile(anyString(), anyString())).thenReturn(imageFile);
        when(fileStorageService.createResource(any(File.class))).thenReturn(imageResource);

        when(imageUploaderService.uploadImage(
                any(MultipartFile.class),
                anyString(),
                anyString()
        )).thenReturn(imageFile);

        // when
        MvcResult result = mockMvc
                .perform(
                        multipart("/api/v1/customers/image")
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
    @DisplayName("Should not upload customer image when file is empty")
    void shouldNotUploadCustomerImageWhenFileIsEmpty() throws Exception {
        // given
        login(customer);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                customer.getPhotoPath(),
                "image/jpeg",
                new byte[0]
        );

        // when
        mockMvc
                .perform(
                        multipart("/api/v1/customers/image")
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
    @DisplayName("Should not upload customer image when size exceeds limit")
    void shouldNotUploadCustomerImageWhenSizeExceedsLimit() throws Exception {
        // given
        login(customer);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                customer.getPhotoPath(),
                "image/jpeg",
                new byte[5 * 1024 * 1024 + 1]
        );

        // when
        mockMvc
                .perform(
                        multipart("/api/v1/customers/image")
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
    @DisplayName("Should not upload image when type is not supported")
    void shouldNotUploadImageWhenTypeIsNotSupported() throws Exception {
        // given
        login(customer);

        //        MockMultipartFile file = ImageTestHelper.createDefaultBmp();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                customer.getPhotoPath(),
                "text/plain",
                new byte[5]
        );

        // when
        mockMvc
                .perform(
                        multipart("/api/v1/customers/image")
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
