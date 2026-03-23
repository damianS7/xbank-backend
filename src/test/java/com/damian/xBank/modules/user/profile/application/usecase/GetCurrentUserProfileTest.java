package com.damian.xBank.modules.user.profile.application.usecase;

import com.damian.xBank.modules.user.profile.application.usecase.get.GetCurrentUserProfile;
import com.damian.xBank.modules.user.profile.application.usecase.get.GetUserProfileQuery;
import com.damian.xBank.modules.user.profile.application.usecase.get.GetUserProfileResult;
import com.damian.xBank.modules.user.profile.domain.exception.UserProfileNotFoundException;
import com.damian.xBank.modules.user.profile.domain.factory.UserProfileFactory;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.domain.model.UserRole;
import com.damian.xBank.modules.user.user.domain.model.UserTestBuilder;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
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
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetCurrentUserProfileTest extends AbstractServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetCurrentUserProfile getCurrentUserProfile;

    private User customer;

    @BeforeEach
    void setUp() {
        UserProfile profile = UserProfileFactory.testProfile();

        customer = UserTestBuilder.builder()
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
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.of(customer));

        GetUserProfileQuery query = new GetUserProfileQuery();
        GetUserProfileResult result = getCurrentUserProfile.execute(query);

        // then
        assertEquals(customer.getProfile().getId(), result.id());
        assertEquals(customer.getProfile().getFirstName(), result.firstName());
        verify(userRepository, times(1)).findById(customer.getId());
    }

    @Test
    @DisplayName("should throw exception when user not found")
    void getProfile_WhenUserNotFound_ThrowsException() {
        // given
        setUpContext(customer);

        GetUserProfileQuery query = new GetUserProfileQuery();

        // when
        when(userRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

        UserProfileNotFoundException exception = assertThrows(
            UserProfileNotFoundException.class,
            () -> getCurrentUserProfile.execute(query)
        );

        // then
        assertEquals(ErrorCodes.PROFILE_NOT_FOUND, exception.getMessage());
    }
}
