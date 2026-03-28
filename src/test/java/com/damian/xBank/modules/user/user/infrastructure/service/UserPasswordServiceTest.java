package com.damian.xBank.modules.user.user.infrastructure.service;

import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.modules.user.user.infrastructure.repository.UserRepository;
import com.damian.xBank.test.AbstractServiceTest;
import com.damian.xBank.test.utils.UserTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserPasswordServiceTest extends AbstractServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserPasswordService userPasswordService;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserTestFactory.aCustomerWithId(1L);
    }

    @Test
    @DisplayName("should update user password")
    void updatePassword_UpdatesPassword() {
        // given
        final String rawNewPassword = "123456789";
        final String encodedNewPassword = bCryptPasswordEncoder.encode(rawNewPassword);

        // when
        when(bCryptPasswordEncoder.encode(rawNewPassword)).
            thenReturn(encodedNewPassword);
        when(userRepository.findById(user.getId()))
            .thenReturn(Optional.of(user));

        // then
        userPasswordService.updatePassword(user.getId(), rawNewPassword);
        assertThat(user.getPasswordHash()).isEqualTo(encodedNewPassword);
        verify(userRepository, times(1)).save(user);
    }
}
