package com.damian.xBank.modules.user.customer.application.service;

import com.damian.xBank.modules.setting.application.service.SettingService;
import com.damian.xBank.modules.user.account.account.application.service.UserAccountService;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;
import com.damian.xBank.modules.user.customer.application.dto.request.CustomerRegistrationRequest;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.modules.user.customer.domain.enums.CustomerGender;
import com.damian.xBank.modules.user.customer.infra.repository.CustomerRepository;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerRegistrationServiceTest extends AbstractServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private SettingService settingService;

    @Mock
    private UserAccountService userAccountService;

    @InjectMocks
    private CustomerRegistrationService customerRegistrationService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = Customer.create()
                           .setId(1L)
                           .setEmail("customer@test.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
                           .setRole(UserAccountRole.USER)
                           .setFirstName("John")
                           .setLastName("Wick")
                           .setGender(CustomerGender.MALE)
                           .setBirthdate(LocalDate.of(1989, 1, 1))
                           .setPhotoPath("avatar.jpg");
    }

    @Test
    @DisplayName("Should create customer")
    void shouldRegisterCustomer() {
        // given
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "david@gmail.com",
                "123456",
                "david",
                "white",
                "123 123 123",
                LocalDate.of(1989, 1, 1),
                CustomerGender.MALE,
                "Fake AV",
                "50120",
                "USA",
                "123123123Z"
        );

        UserAccount givenUserAccount = UserAccount.create()
                                                  .setEmail(request.email());

        // when
        doNothing().when(settingService).initializeDefaultSettingsFor(any(UserAccount.class));
        when(userAccountService.createUserAccount(anyString(), anyString(), any()))
                .thenReturn(givenUserAccount);

        customerRegistrationService.registerCustomer(request);

        // then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerArgumentCaptor.capture());

        Customer customer = customerArgumentCaptor.getValue();
        verify(customerRepository, times(1)).save(any(Customer.class));
        assertThat(customer)
                .isNotNull()
                .extracting(
                        Customer::getEmail,
                        Customer::getFirstName,
                        Customer::getLastName
                ).containsExactly(
                        request.email(),
                        request.firstName(),
                        request.lastName()
                );
    }
}
