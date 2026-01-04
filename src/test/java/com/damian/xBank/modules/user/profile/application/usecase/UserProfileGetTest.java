package com.damian.xBank.modules.user.profile.application.usecase;

import com.damian.xBank.modules.user.profile.domain.exception.UserProfileNotFoundException;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.profile.infrastructure.repository.UserProfileRepository;
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
    @DisplayName("")
    void getProfile_WhenCustomerExists_ReturnsProfile() {
        // given
        // when
        when(userProfileRepository.findByUserId(anyLong()))
                .thenReturn(Optional.of(customer.getProfile()));

        UserProfile result = userProfileGet.execute();

        // then
        verify(userProfileRepository, times(1)).findById(customer.getId());
        assertEquals(customer.getId(), result.getId());
        assertEquals(customer.getEmail(), result.getUser().getEmail());
    }

    @Test
    @DisplayName("")
    void getProfile_WhenCustomerNotExists_ThrowsException() {
        // given
        customer.setId(-1L);

        // when
        UserProfileNotFoundException exception = assertThrows(
                UserProfileNotFoundException.class,
                () -> userProfileGet.execute()
        );

        // then
        assertEquals(ErrorCodes.PROFILE_NOT_FOUND, exception.getMessage());
    }
}
