package com.damian.xBank.modules.user.customer.infra.controller;

import com.damian.xBank.modules.user.customer.application.dto.mapper.CustomerDtoMapper;
import com.damian.xBank.modules.user.customer.application.dto.request.CustomerRegistrationRequest;
import com.damian.xBank.modules.user.customer.application.dto.response.CustomerDetailDto;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.modules.user.customer.application.service.CustomerRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
public class CustomerRegistrationController {
    private final CustomerRegistrationService customerRegistrationService;

    @Autowired
    public CustomerRegistrationController(
            CustomerRegistrationService customerRegistrationService
    ) {
        this.customerRegistrationService = customerRegistrationService;
    }

    // endpoint for the current user to upload his profile photo
    @PostMapping("/customers/register")
    public ResponseEntity<?> registerCustomer(
            @Validated @RequestBody
            CustomerRegistrationRequest request
    ) {

        Customer registeredCustomer = customerRegistrationService.registerCustomer(request);
        CustomerDetailDto customerDto = CustomerDtoMapper.toCustomerWithAccountDto(registeredCustomer);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customerDto);
    }
}

