package com.damian.xBank.modules.user.customer;

import com.damian.xBank.modules.user.account.account.enums.UserAccountRole;
import com.damian.xBank.modules.user.account.account.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.account.account.repository.UserAccountRepository;
import com.damian.xBank.modules.user.account.account.service.UserAccountService;
import com.damian.xBank.modules.user.customer.dto.request.CustomerUpdateRequest;
import com.damian.xBank.modules.user.customer.enums.CustomerGender;
import com.damian.xBank.modules.user.customer.exception.CustomerNotFoundException;
import com.damian.xBank.modules.user.customer.exception.CustomerUpdateAuthorizationException;
import com.damian.xBank.modules.user.customer.exception.CustomerUpdateException;
import com.damian.xBank.modules.user.customer.repository.CustomerRepository;
import com.damian.xBank.modules.user.customer.service.CustomerService;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.domain.Customer;
import com.damian.xBank.shared.exception.Exceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest extends AbstractServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private UserAccountService userAccountService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = Customer.create()
                           .setId(1L)
                           .setEmail("customer@test.com")
                           .setPassword(passwordEncoder.encode(RAW_PASSWORD))
                           .setRole(UserAccountRole.USER)
                           .setFirstName("John")
                           .setLastName("Wick")
                           .setGender(CustomerGender.MALE)
                           .setBirthdate(LocalDate.of(1989, 1, 1))
                           .setPhotoPath("avatar.jpg");
    }

    @Test
    @DisplayName("Should get all customers")
    void shouldGetAllCustomers() {
        // given
        Customer givenCustomer1 = Customer.create()
                                          .setId(1L)
                                          .setEmail("customer1@demo.com")
                                          .setPassword(RAW_PASSWORD);

        Customer givenCustomer2 = Customer.create()
                                          .setId(2L)
                                          .setEmail("customer2@demo.com")
                                          .setPassword(RAW_PASSWORD);

        List<Customer> customerList = List.of(
                givenCustomer1, givenCustomer2
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> customerPage = new PageImpl<>(customerList, pageable, customerList.size());

        // when
        when(customerRepository.findAll(pageable)).thenReturn(customerPage);
        Page<Customer> result = customerService.getCustomers(pageable);

        // then
        assertThat(result)
                .isNotNull()
                .extracting(
                        Page::getTotalElements
                ).isEqualTo(
                        result.getTotalElements()
                );
        assertEquals(givenCustomer1.getEmail(), result.getContent().get(0).getAccount().getEmail());
        assertEquals(givenCustomer2.getEmail(), result.getContent().get(1).getAccount().getEmail());
        verify(customerRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should find customer")
    void shouldGetCustomer() {
        // given
        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setEmail("customer1@demo.com")
                                    .setPassword(RAW_PASSWORD);

        // when
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        Customer storedCustomer = customerService.getCustomer(customer.getId());

        // then
        verify(customerRepository, times(1)).findById(customer.getId());
        assertEquals(customer.getId(), storedCustomer.getId());
        assertEquals(customer.getAccount().getEmail(), storedCustomer.getAccount().getEmail());
    }

    @Test
    @DisplayName("Should not find customer when not exist")
    void shouldNotGetCustomerWhenNotExist() {
        // given
        Long id = -1L;

        // when
        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getCustomer(id)
        );

        // then
        assertTrue(exception.getMessage().contains("Customer not found"));
    }

    @Test
    @DisplayName("Should delete customer")
    void shouldDeleteCustomer() {
        // given
        Long id = 7L;
        when(customerRepository.existsById(id)).thenReturn(true);

        // when
        boolean isDeleted = customerService.deleteCustomer(id);

        // then
        verify(customerRepository, times(1)).deleteById(id);
        verify(customerRepository).deleteById(id);
        assertThat(isDeleted).isTrue();
    }

    @Test
    @DisplayName("Should not delete customer when not exist")
    void shouldNotDeleteCustomerWhenNotExist() {
        // given
        Long id = -1L;

        // when
        when(customerRepository.existsById(id)).thenReturn(false);

        // then
        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.deleteCustomer(id)
        );
        assertTrue(exception.getMessage().contains("Customer not found"));
        verify(customerRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should update customer")
    void shouldUpdateCustomer() {
        // given
        setUpContext(customer);

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "David");
        fields.put("lastName", "David");
        fields.put("birthdate", "1904-01-02");
        fields.put("gender", "MALE");
        fields.put("phoneNumber", "9199191919");
        CustomerUpdateRequest givenRequest = new CustomerUpdateRequest(
                RAW_PASSWORD,
                fields
        );

        // when
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer result = customerService.updateCustomer(givenRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo(givenRequest.fieldsToUpdate().get("firstName"));
        assertThat(result.getLastName()).isEqualTo(givenRequest.fieldsToUpdate().get("lastName"));
        assertThat(result.getPhone()).isEqualTo(givenRequest.fieldsToUpdate().get("phoneNumber"));
        assertThat(result.getBirthdate().toString()).isEqualTo(givenRequest.fieldsToUpdate().get("birthdate"));
        assertThat(result.getGender().toString()).isEqualTo(givenRequest.fieldsToUpdate().get("gender"));
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    @DisplayName("Should not update customer when password is wrong")
    void shouldNotUpdateUserWhenPasswordIsWrong() {
        // given
        setUpContext(customer);

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "David");
        CustomerUpdateRequest givenRequest = new CustomerUpdateRequest(
                "wrongPassword1",
                fields
        );

        // when
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        UserAccountInvalidPasswordConfirmationException exception = assertThrows(
                UserAccountInvalidPasswordConfirmationException.class,
                () -> customerService.updateCustomer(givenRequest)
        );

        // Then
        assertEquals(Exceptions.USER.ACCOUNT.INVALID_PASSWORD, exception.getMessage());
    }

    @Test
    @DisplayName("Should not update customer when customer not found")
    void shouldNotUpdateUserWhenUserNotFound() {
        // given
        setUpContext(customer);

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "David");
        CustomerUpdateRequest givenRequest = new CustomerUpdateRequest(
                RAW_PASSWORD,
                fields
        );

        // when
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.empty());
        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.updateCustomer(givenRequest)
        );

        // Then
        assertEquals(Exceptions.CUSTOMER.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should not update customer when customer not found")
    void shouldNotUpdateUserWhenYouAreNotOwner() {
        // given
        setUpContext(customer);

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "David");
        CustomerUpdateRequest givenRequest = new CustomerUpdateRequest(
                RAW_PASSWORD,
                fields
        );

        Customer givenCustomer = Customer.create()
                                         .setId(5L)
                                         .setEmail("customer@test.com")
                                         .setPassword(RAW_PASSWORD);

        // when
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(givenCustomer));
        CustomerUpdateAuthorizationException exception = assertThrows(
                CustomerUpdateAuthorizationException.class,
                () -> customerService.updateCustomer(givenRequest)
        );

        // Then
        assertEquals(Exceptions.CUSTOMER.NOT_OWNER, exception.getMessage());
    }

    @Test
    @DisplayName("Should not update customer when customer not found")
    void shouldNotUpdateUserWhenInvalidField() {
        // given
        setUpContext(customer);

        Map<String, Object> fields = new HashMap<>();
        fields.put("firstName", "David");
        fields.put("fakeField", "1234");
        CustomerUpdateRequest givenRequest = new CustomerUpdateRequest(
                RAW_PASSWORD,
                fields
        );

        // when
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        CustomerUpdateException exception = assertThrows(
                CustomerUpdateException.class,
                () -> customerService.updateCustomer(givenRequest)
        );

        // Then
        assertEquals(Exceptions.CUSTOMER.UPDATE_FAILED, exception.getMessage());
    }
}
