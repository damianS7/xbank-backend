package com.damian.xBank.modules.auth.admin;

import com.damian.xBank.modules.auth.AuthenticationService;
import com.damian.xBank.modules.customer.http.request.CustomerPasswordUpdateRequest;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/admin")
public class AuthenticationAdminController {

    private final AuthenticationService authenticationService;

    public AuthenticationAdminController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    // endpoint to modify customer password
    @PatchMapping("/auth/customers/{id}/password")
    public ResponseEntity<?> updateCustomerPassword(
            @PathVariable @Positive
            Long id,
            @Validated @RequestBody
            CustomerPasswordUpdateRequest request
    ) {
        authenticationService.updatePassword(id, request.newPassword());

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}