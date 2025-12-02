package com.damian.xBank.modules.user.customer.controller;

import com.damian.xBank.modules.user.customer.dto.mapper.CustomerDtoMapper;
import com.damian.xBank.modules.user.customer.dto.request.CustomerUpdateRequest;
import com.damian.xBank.modules.user.customer.dto.response.CustomerDetailDto;
import com.damian.xBank.modules.user.customer.model.Customer;
import com.damian.xBank.modules.user.customer.service.CustomerImageService;
import com.damian.xBank.modules.user.customer.service.CustomerService;
import com.damian.xBank.shared.utils.ImageHelper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@RequestMapping("/api/v1")
@RestController
public class CustomerController {
    private final CustomerService customerService;
    private final CustomerImageService customerImageService;

    @Autowired
    public CustomerController(
            CustomerService customerService,
            CustomerImageService customerImageService
    ) {
        this.customerService = customerService;
        this.customerImageService = customerImageService;
    }

    // endpoint to receive current customer
    @GetMapping("/customers")
    public ResponseEntity<?> getLoggedCustomerData() {
        Customer customer = customerService.getCustomer();
        CustomerDetailDto dto = CustomerDtoMapper.toCustomerWithAccountDto(customer);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    // endpoint to modify current customer profile
    @PatchMapping("/customers")
    public ResponseEntity<CustomerDetailDto> update(
            @Validated @RequestBody
            CustomerUpdateRequest request
    ) {
        Customer customer = customerService.updateCustomer(request);
        //        CustomerDto customerDto = CustomerDtoMapper.toCustomerDto(customer);
        CustomerDetailDto customerDto = CustomerDtoMapper.toCustomerWithAccountDto(customer);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(customerDto);
    }

    // endpoint to get the current customer profile image
    @GetMapping("/customers/{customerId}/image")
    public ResponseEntity<?> getProfileImage(
            @PathVariable @NotNull @Positive
            Long customerId
    ) {
        Resource resource = customerImageService.getUserImage(customerId);
        String contentType = ImageHelper.getContentType(resource);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic())
                .body(resource);
    }

    // endpoint to get the current user profile image
    @GetMapping("/customers/image")
    public ResponseEntity<?> getProfileImage() {
        Resource resource = customerImageService.getUserImage();
        String contentType = ImageHelper.getContentType(resource);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic())
                .body(resource);
    }

    // endpoint for the current user to upload his profile photo
    @PostMapping("/customers/image")
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("currentPassword") @NotBlank
            String currentPassword,
            @RequestParam("file") MultipartFile file
    ) {
        customerImageService.uploadUserImage(currentPassword, file);
        Resource resource = customerImageService.getUserImage();
        String contentType = ImageHelper.getContentType(resource);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}

