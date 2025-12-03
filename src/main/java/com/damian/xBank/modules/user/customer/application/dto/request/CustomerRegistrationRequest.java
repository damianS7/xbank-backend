package com.damian.xBank.modules.user.customer.application.dto.request;

import com.damian.xBank.modules.user.customer.domain.enums.CustomerGender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/**
 * Contains all the data required for Customer registration
 */
public record CustomerRegistrationRequest(
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be a well-formed email address.")
        String email,

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must be at least 8 characters long, contain at least one uppercase letter, " +
                          "one number, and one special character."
        )
        String password,

        @NotBlank(message = "Firstname must not be blank.")
        String firstName,

        @NotBlank(message = "Lastname must not be blank.")
        String lastName,

        @NotBlank(message = "Phone must not be blank.")
        String phoneNumber,

        @NotNull(message = "Birthdate must not be null.")
        LocalDate birthdate,

        @NotNull(message = "Gender must not be null")
        CustomerGender gender,

        @NotBlank(message = "Address must not be blank.")
        String address,

        @NotBlank(message = "Postal code must not be blank.")
        String zipCode,

        @NotBlank(message = "Country must not be blank.")
        String country,

        @NotBlank(message = "National ID must not be blank.")
        String nationalId
) {
}
