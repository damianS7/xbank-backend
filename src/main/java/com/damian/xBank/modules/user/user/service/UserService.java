package com.damian.whatsapp.modules.user.user.service;

import com.damian.whatsapp.modules.user.account.account.dto.request.UserAccountRegistrationRequest;
import com.damian.whatsapp.modules.user.account.account.exception.UserAccountEmailTakenException;
import com.damian.whatsapp.modules.user.user.dto.request.UserUpdateRequest;
import com.damian.whatsapp.modules.user.user.enums.UserGender;
import com.damian.whatsapp.modules.user.user.exception.UserAuthorizationException;
import com.damian.whatsapp.modules.user.user.exception.UserNotFoundException;
import com.damian.whatsapp.modules.user.user.exception.UserUpdateException;
import com.damian.whatsapp.modules.user.user.repository.UserRepository;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.exception.Exceptions;
import com.damian.whatsapp.shared.util.AuthHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(
            UserRepository userRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * Creates a new user
     *
     * @param request contains the fields needed for the user creation
     * @return the user created
     * @throws UserAccountEmailTakenException if another user has the email
     */
    public User createUser(UserAccountRegistrationRequest request) {
        log.debug("Creating user with email: {}", request.email());

        // check if the email is already taken
        if (userRepository.existsByUserAccount_Email(request.email())) {
            throw new UserAccountEmailTakenException(
                    Exceptions.USER.EMAIL_TAKEN, request.email()
            );
        }

        // we create the user and assign the data
        User user = new User();
        user.setUserName(request.userName());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhone(request.phone());
        user.setGender(request.gender());
        user.setBirthdate(request.birthdate());
        user.setEmail(request.email());
        user.setPassword(bCryptPasswordEncoder.encode(request.password()));
        user.setCreatedAt(Instant.now());

        return userRepository.save(user);
    }

    /**
     * Deletes a user
     *
     * @param userId the id of the user to be deleted
     * @throws UserNotFoundException if the user does not exist or if the logged user is not ADMIN
     */
    public void deleteUser(Long userId) {
        log.debug("Deleting user: {}", userId);
        // if the user does not exist we throw an exception
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    Exceptions.USER.NOT_FOUND, userId
            );
        }

        // we delete the user
        userRepository.deleteById(userId);
    }

    /**
     * Returns all the user
     *
     * @return a list of UserDTO
     * @throws UserNotFoundException if the logged user is not ADMIN
     */
    public Page<User> getUsers(Pageable pageable) {
        // we return all the user
        return userRepository.findAll(pageable);
    }

    /**
     * Returns a user
     *
     * @param userId the id of the user to be returned
     * @return the user
     * @throws UserNotFoundException if the user does not exist or if the logged user is not ADMIN
     */
    public User getUser(Long userId) {
        log.debug("Getting user: {}", userId);
        // if the user does not exist we throw an exception
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(
                        Exceptions.USER.NOT_FOUND, userId
                )
        );
    }

    // returns the logged user
    public User getUser() {
        User loggedUser = AuthHelper.getLoggedUser();
        return this.getUser(loggedUser.getId());
    }

    /**
     * It updates the current user profile
     *
     * @param request the request containing the updated profile information
     * @return User the updated profile
     */
    public User updateUser(UserUpdateRequest request) {
        final User currentUser = AuthHelper.getLoggedUser();

        return this.updateUser(currentUser.getId(), request);
    }

    /**
     * It updates the user profile by id
     *
     * @param userId  the id of the profile to be updated
     * @param request the request containing the updated profile information
     * @return User the updated profile
     * @throws UserNotFoundException if the profile is not found
     */
    public User updateUser(Long userId, UserUpdateRequest request) {
        final User currentUser = AuthHelper.getLoggedUser();
        log.debug("user: {} updating profile: {}", currentUser.getId(), userId);

        // find the profile we want to modify
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        Exceptions.USER.NOT_FOUND, userId)
                );


        // if the logged user is not admin
        if (!AuthHelper.isAdmin(currentUser)) {
            // we make sure that this profile belongs to the user logged
            if (!user.getId().equals(currentUser.getId())) {
                throw new UserAuthorizationException(
                        Exceptions.USER.NOT_OWNER,
                        userId
                );
            }

            // we validate the password before updating the profile
            AuthHelper.validatePassword(currentUser, request.currentPassword());
        }

        // we iterate over the fields (if any)
        request.fieldsToUpdate().forEach((key, value) -> {
            switch (key) {
                case "firstName" -> user.setFirstName((String) value);
                case "lastName" -> user.setLastName((String) value);
                case "userName" -> user.setUserName((String) value);
                case "phone" -> user.setPhone((String) value);
                case "avatarFilename" -> user.setImageFilename((String) value);
                case "gender" -> user.setGender(UserGender.valueOf((String) value));
                case "birthdate" -> user.setBirthdate(LocalDate.parse((String) value));
                default -> throw new UserUpdateException(
                        Exceptions.USER.UPDATE_FAILED_INVALID_FIELD, userId
                );
            }
        });

        // we change the updateAt timestamp field
        user.setUpdatedAt(Instant.now());

        // we save the updated profile to the database
        return userRepository.save(user);
    }
}
