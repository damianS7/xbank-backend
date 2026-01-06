package com.damian.xBank.modules.user.profile.application.usecase;

import com.damian.xBank.modules.user.profile.domain.exception.UserProfileNotFoundException;
import com.damian.xBank.modules.user.profile.domain.factory.UserProfileFactory;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.profile.infrastructure.repository.UserProfileRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserProfileGetTest extends AbstractServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileGet userProfileGet;

    private User customer;

    @BeforeEach
    void setUp() {
        UserProfile profile = UserProfileFactory.testProfile();

        customer = UserTestBuilder.aCustomer()
                                  .withId(1L)
                                  .withEmail("customer@test.com")
                                  .withPassword(this.bCryptPasswordEncoder.encode(RAW_PASSWORD))
                                  .withRole(UserRole.CUSTOMER)
                                  .withProfile(profile)
                                  .build();
    }

    @Test
    @DisplayName("should return profile when user exists")
    void getProfile_WhenUserExists_ReturnsProfile() {
        // given
        setUpContext(customer);

        // when
        when(userProfileRepository.findByUserId(anyLong()))
                .thenReturn(Optional.of(customer.getProfile()));

        UserProfile result = userProfileGet.execute();

        // then
        assertEquals(customer.getId(), result.getUser().getId());
        assertEquals(customer.getEmail(), result.getUser().getEmail());
        verify(userProfileRepository, times(1)).findByUserId(customer.getId());
    }

    @Test
    @DisplayName("should throw exception when user not found")
    void getProfile_WhenUserNotFound_ThrowsException() {
        // given
        setUpContext(customer);

        // when
        when(userProfileRepository.findByUserId(anyLong()))
                .thenReturn(Optional.empty());

        UserProfileNotFoundException exception = assertThrows(
                UserProfileNotFoundException.class,
                () -> userProfileGet.execute()
        );

        // then
        assertEquals(ErrorCodes.PROFILE_NOT_FOUND, exception.getMessage());
    }
}
