package com.damian.xBank.modules.user.profile.application.usecase;

import com.damian.xBank.modules.user.profile.application.dto.request.UserProfileUpdateRequest;
import com.damian.xBank.modules.user.profile.domain.exception.UserProfileNotFoundException;
import com.damian.xBank.modules.user.profile.domain.exception.UserProfileNotOwnerException;
import com.damian.xBank.modules.user.profile.domain.exception.UserProfileUpdateException;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.profile.infrastructure.repository.UserProfileRepository;
import com.damian.xBank.modules.user.user.domain.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserAccountRole;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.UserProfileTestFactory;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserProfileUpdateTest extends AbstractServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileUpdate userProfileUpdate;

    private User customer;

    @BeforeEach
    void setUp() {
        UserProfile profile = UserProfileTestFactory.aProfile();

        customer = UserTestBuilder.aCustomer()
                                  .withId(1L)
                                  .withEmail("customer@test.com")
                                  .withPassword(this.bCryptPasswordEncoder.encode(RAW_PASSWORD))
                                  .withRole(UserAccountRole.CUSTOMER)
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
        UserProfileUpdateRequest givenRequest = new UserProfileUpdateRequest(
                RAW_PASSWORD,
                fields
        );

        // when
        when(userProfileRepository.findByUserId(customer.getId()))
                .thenReturn(Optional.of(customer.getProfile()));

        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        UserProfile result = userProfileUpdate.execute(givenRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo(givenRequest.fieldsToUpdate().get("firstName"));
        assertThat(result.getLastName()).isEqualTo(givenRequest.fieldsToUpdate().get("lastName"));
        assertThat(result.getPhone()).isEqualTo(givenRequest.fieldsToUpdate().get("phoneNumber"));
        assertThat(result.getBirthdate().toString()).isEqualTo(givenRequest.fieldsToUpdate().get("birthdate"));
        assertThat(result.getGender().toString()).isEqualTo(givenRequest.fieldsToUpdate().get("gender"));
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }

    @Test
    @DisplayName("should throw exception when invalid field")
    void updateProfile_WhenInvalidField_ThrowsException() {
        // given
        setUpContext(customer);

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "David");
        fields.put("fakeField", "1234");
        UserProfileUpdateRequest givenRequest = new UserProfileUpdateRequest(
                RAW_PASSWORD,
                fields
        );

        // when
        when(userProfileRepository.findByUserId(customer.getId())).thenReturn(Optional.of(customer.getProfile()));
        UserProfileUpdateException exception = assertThrows(
                UserProfileUpdateException.class,
                () -> userProfileUpdate.execute(givenRequest)
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
        UserProfileUpdateRequest givenRequest = new UserProfileUpdateRequest(
                "wrongPassword1",
                fields
        );

        // when
        when(userProfileRepository.findByUserId(customer.getId())).thenReturn(Optional.of(customer.getProfile()));

        //        doThrow(
        //                new UserAccountInvalidPasswordConfirmationException(
        //                        Exceptions.USER.ACCOUNT.INVALID_PASSWORD,
        //                        customer.getId()
        //                )
        //        ).when(authenticationContext).validatePassword(
        //                any(Customer.class), anyString());

        UserAccountInvalidPasswordConfirmationException exception = assertThrows(
                UserAccountInvalidPasswordConfirmationException.class,
                () -> userProfileUpdate.execute(givenRequest)
        );

        // Then
        assertEquals(ErrorCodes.USER_ACCOUNT_INVALID_PASSWORD, exception.getMessage());
    }

    @Test
    @DisplayName("should throw exception when user not found")
    void updateProfile_WhenUserNotFound_ThrowsException() {
        // given
        setUpContext(customer);

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "David");
        UserProfileUpdateRequest givenRequest = new UserProfileUpdateRequest(
                RAW_PASSWORD,
                fields
        );

        // when
        when(userProfileRepository.findByUserId(customer.getId())).thenReturn(Optional.empty());
        UserProfileNotFoundException exception = assertThrows(
                UserProfileNotFoundException.class,
                () -> userProfileUpdate.execute(givenRequest)
        );

        // Then
        assertEquals(ErrorCodes.PROFILE_NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("should throw exception when user not owner")
    void updateProfile_WhenUserNotOwner_ThrowsException() {
        // given
        setUpContext(customer);

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "David");
        UserProfileUpdateRequest givenRequest = new UserProfileUpdateRequest(
                RAW_PASSWORD,
                fields
        );

        UserProfile profile = UserProfileTestFactory.aProfile();

        User givenCustomer = UserTestBuilder.aCustomer()
                                            .withId(5L)
                                            .withEmail("customer@test.com")
                                            .withPassword(this.bCryptPasswordEncoder.encode(RAW_PASSWORD))
                                            .withRole(UserAccountRole.CUSTOMER)
                                            .withProfile(profile)
                                            .build();

        // when TODO: review this
        when(userProfileRepository.findByUserId(customer.getId()))
                .thenReturn(Optional.of(givenCustomer.getProfile()));

        UserProfileNotOwnerException exception = assertThrows(
                UserProfileNotOwnerException.class,
                () -> userProfileUpdate.execute(givenRequest)
        );

        // Then
        assertEquals(ErrorCodes.PROFILE_UPDATE_FAILED, exception.getMessage());
    }
}