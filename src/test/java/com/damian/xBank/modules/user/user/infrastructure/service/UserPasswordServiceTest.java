package com.damian.xBank.modules.user.user.infrastructure.service;

import com.damian.xBank.modules.user.profile.domain.factory.UserProfileFactory;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.utils.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserPasswordServiceTest extends AbstractServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserPasswordService userPasswordService;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserTestBuilder.aCustomer()
                              .withId(1L)
                              .withPassword(RAW_PASSWORD)
                              .withEmail("user@demo.com")
                              .withProfile(UserProfileFactory.testProfile())
                              .build();
    }

    @Test
    @DisplayName("should update user password")
    void updatePassword_WhenValid_UpdatesPassword() {
        // given
        final String rawNewPassword = "123456789";
        final String encodedNewPassword = bCryptPasswordEncoder.encode(rawNewPassword);

        // when
        when(bCryptPasswordEncoder.encode(rawNewPassword)).thenReturn(encodedNewPassword);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userPasswordService.updatePassword(user.getId(), rawNewPassword);

        // then
        assertThat(user.getPassword()).isEqualTo(encodedNewPassword);
        verify(userRepository, times(1)).save(user);
    }
}
