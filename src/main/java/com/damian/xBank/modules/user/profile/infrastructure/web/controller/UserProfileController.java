package com.damian.xBank.modules.user.profile.infrastructure.web.controller;

import com.damian.xBank.modules.user.profile.application.dto.mapper.UserProfileDtoMapper;
import com.damian.xBank.modules.user.profile.application.dto.request.UserProfileUpdateRequest;
import com.damian.xBank.modules.user.profile.application.dto.response.UserProfileDetailDto;
import com.damian.xBank.modules.user.profile.application.usecase.UserProfileGet;
import com.damian.xBank.modules.user.profile.application.usecase.UserProfileUpdate;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
public class UserProfileController {
    private final UserProfileGet userProfileGet;
    private final UserProfileUpdate userProfileUpdate;

    @Autowired
    public UserProfileController(
            UserProfileGet userProfileGet,
            UserProfileUpdate userProfileUpdate
    ) {
        this.userProfileGet = userProfileGet;
        this.userProfileUpdate = userProfileUpdate;
    }

    // endpoint to receive current customer
    @GetMapping("/profiles")
    public ResponseEntity<?> getLoggedCustomerData() {
        UserProfile customer = userProfileGet.execute();
        UserProfileDetailDto dto = UserProfileDtoMapper.toCustomerWithAccountDto(customer);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    // endpoint to modify current customer profile
    @PatchMapping("/profiles")
    public ResponseEntity<UserProfileDetailDto> update(
            @Validated @RequestBody
            UserProfileUpdateRequest request
    ) {
        UserProfile customer = userProfileUpdate.execute(request);
        //        CustomerDto customerDto = CustomerDtoMapper.toCustomerDto(customer);
        UserProfileDetailDto customerDto = UserProfileDtoMapper.toCustomerWithAccountDto(customer);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(customerDto);
    }
}

