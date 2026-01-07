package com.damian.xBank.modules.user.profile.application.usecase;

import com.damian.xBank.modules.user.profile.application.dto.request.UserProfileUpdateRequest;
import com.damian.xBank.modules.user.profile.domain.exception.UserProfileNotFoundException;
import com.damian.xBank.modules.user.profile.domain.exception.UserProfileUpdateException;
import com.damian.xBank.modules.user.profile.domain.model.UserGender;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.profile.infrastructure.repository.UserProfileRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;

@Service
public class UserProfileUpdate {
    private static final Logger log = LoggerFactory.getLogger(UserProfileUpdate.class);
    private final UserProfileRepository userProfileRepository;
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;

    public UserProfileUpdate(
            UserProfileRepository userProfileRepository,
            AuthenticationContext authenticationContext,
            PasswordValidator passwordValidator
    ) {
        this.userProfileRepository = userProfileRepository;
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
    }

    public UserProfile execute(UserProfileUpdateRequest request) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        return execute(currentUser.getId(), request);
    }

    /**
     * It updates the customer profile by its ID.
     *
     * @param userId  the id of the profile to be updated
     * @param request the request containing the updated profile information
     * @return Customer with the updated profile
     * @throws UserProfileNotFoundException if the profile is not found
     */
    public UserProfile execute(Long userId, UserProfileUpdateRequest request) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // find the user we want to modify
        UserProfile profile = userProfileRepository
                .findByUserId(userId)
                .orElseThrow(
                        () -> new UserProfileNotFoundException(userId)
                );

        if (!currentUser.isAdmin()) {
            // we make sure that this profile belongs to the current user
            profile.assertOwnedBy(currentUser.getId());

            // we validate the password before updating the profile
            passwordValidator.validatePassword(currentUser, request.currentPassword());
        }

        // we iterate over the fields (if any)
        request.fieldsToUpdate().forEach((key, value) -> {
            switch (key) {
                case "firstName" -> profile.setFirstName((String) value);
                case "lastName" -> profile.setLastName((String) value);
                case "phoneNumber" -> profile.setPhoneNumber((String) value);
                case "country" -> profile.setCountry((String) value);
                case "zipCode" -> profile.setPostalCode((String) value);
                case "address" -> profile.setAddress((String) value);
                case "photo" -> profile.setPhotoPath((String) value);
                case "gender" -> profile.setGender(UserGender.valueOf((String) value));
                case "birthdate" -> profile.setBirthdate(LocalDate.parse((String) value));
                default -> throw new UserProfileUpdateException(
                        userId, new Object[]{key, value.toString()}
                );
            }
        });

        // we change the updateAt timestamp field
        profile.setUpdatedAt(Instant.now());

        // we save the updated profile to the database
        return userProfileRepository.save(profile);
    }
}
