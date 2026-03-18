package com.damian.xBank.modules.user.profile.application.usecase;

import com.damian.xBank.modules.user.profile.application.usecase.update.UpdateCurrentUserProfile;
import com.damian.xBank.modules.user.profile.application.usecase.update.UpdateUserProfileCommand;
import com.damian.xBank.modules.user.profile.application.usecase.update.UpdateUserProfileResult;
import com.damian.xBank.modules.user.profile.domain.exception.UserProfileNotFoundException;
import com.damian.xBank.modules.user.profile.domain.exception.UserProfileUpdateException;
import com.damian.xBank.modules.user.profile.domain.factory.UserProfileFactory;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.user.domain.exception.UserInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserTestBuilder;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateCurrentUserProfileTest extends AbstractServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UpdateCurrentUserProfile updateCurrentUserProfile;

    private User customer;

    @BeforeEach
    void setUp() {
        UserProfile profile = UserProfileFactory.testProfile();

        customer = UserTestBuilder.builder()
            .withId(1L)
            .withEmail("customer@test.com")
            .withPassword(RAW_PASSWORD)
            .withRole(UserRole.CUSTOMER)
            .withProfile(profile)
            .build();
    }

    @Test
    @DisplayName("should return updated profile when valid request")
    void updateProfile_WhenValidRequest_ReturnsUpdatedProfile() {
        // given
        setUpContext(customer);

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "David");
        fields.put("lastName", "David");
        fields.put("birthdate", "1904-01-02");
        fields.put("gender", "MALE");
        fields.put("phoneNumber", "9199191919");
        UpdateUserProfileCommand command = new UpdateUserProfileCommand(
            RAW_PASSWORD,
            fields
        );

        // when
        when(userRepository.findById(customer.getId()))
            .thenReturn(Optional.of(customer));

        when(userRepository.save(any(User.class))).thenAnswer(
            invocation -> invocation.getArgument(0)
        );

        UpdateUserProfileResult result = updateCurrentUserProfile.execute(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo(command.fieldsToUpdate().get("firstName"));
        assertThat(result.lastName()).isEqualTo(command.fieldsToUpdate().get("lastName"));
        assertThat(result.phone()).isEqualTo(command.fieldsToUpdate().get("phoneNumber"));
        assertThat(result.birthdate().toString()).isEqualTo(command.fieldsToUpdate().get("birthdate"));
        assertThat(result.gender().toString()).isEqualTo(command.fieldsToUpdate().get("gender"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("should throw exception when invalid field")
    void updateProfile_WhenInvalidField_ThrowsException() {
        // given
        setUpContext(customer);

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "David");
        fields.put("fakeField", "1234");
        UpdateUserProfileCommand command = new UpdateUserProfileCommand(
            RAW_PASSWORD,
            fields
        );

        // when
        when(userRepository.findById(customer.getId()))
            .thenReturn(Optional.of(customer));

        UserProfileUpdateException exception = assertThrows(
            UserProfileUpdateException.class,
            () -> updateCurrentUserProfile.execute(command)
        );

        // Then
        assertEquals(ErrorCodes.PROFILE_UPDATE_FAILED, exception.getMessage());
    }

    @Test
    @DisplayName("should throw exception when invalid password")
    void updateProfile_WhenInvalidPassword_ThrowsException() {
        // given
        setUpContext(customer);

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "David");
        UpdateUserProfileCommand command = new UpdateUserProfileCommand(
            "wrongPassword1",
            fields
        );

        // when
        when(userRepository.findById(customer.getId()))
            .thenReturn(Optional.of(customer));

        //        doThrow(
        //                new UserAccountInvalidPasswordConfirmationException(
        //                        Exceptions.USER.ACCOUNT.INVALID_PASSWORD,
        //                        customer.getId()
        //                )
        //        ).when(authenticationContext).validatePassword(
        //                any(Customer.class), anyString());

        UserInvalidPasswordConfirmationException exception = assertThrows(
            UserInvalidPasswordConfirmationException.class,
            () -> updateCurrentUserProfile.execute(command)
        );

        // Then
        assertEquals(ErrorCodes.USER_INVALID_PASSWORD, exception.getMessage());
    }

    @Test
    @DisplayName("should throw exception when user not found")
    void updateProfile_WhenUserNotFound_ThrowsException() {
        // given
        setUpContext(customer);

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "David");
        UpdateUserProfileCommand command = new UpdateUserProfileCommand(
            RAW_PASSWORD,
            fields
        );

        // when
        when(userRepository.findById(customer.getId())).thenReturn(Optional.empty());
        UserProfileNotFoundException exception = assertThrows(
            UserProfileNotFoundException.class,
            () -> updateCurrentUserProfile.execute(command)
        );

        // Then
        assertEquals(ErrorCodes.PROFILE_NOT_FOUND, exception.getMessage());
    }

}