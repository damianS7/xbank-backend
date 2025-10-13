package com.damian.xBank.modules.user.customer;

import com.damian.xBank.modules.user.customer.dto.request.CustomerRegistrationRequest;
import com.damian.xBank.modules.user.customer.enums.CustomerGender;
import com.damian.xBank.modules.user.customer.exception.CustomerEmailTakenException;
import com.damian.xBank.modules.user.customer.exception.CustomerNotFoundException;
import com.damian.xBank.modules.user.customer.repository.CustomerRepository;
import com.damian.xBank.modules.user.customer.service.CustomerService;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.domain.Customer;
import com.damian.xBank.shared.exception.Exceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest extends AbstractServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        customerRepository.deleteAll();
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
    void shouldFindCustomer() {
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
    void shouldNotFindCustomerWhenNotExist() {
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
    @DisplayName("Should create customer")
    void shouldCreateCustomer() {
        // given
        final String passwordHash = "¢5554ml;f;lsd";
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "david@gmail.com",
                "123456",
                "david",
                "white",
                "123 123 123",
                LocalDate.of(1989, 1, 1),
                CustomerGender.MALE,
                "",
                "Fake AV",
                "50120",
                "USA",
                "123123123Z"
        );

        // when
        when(bCryptPasswordEncoder.encode(request.password())).thenReturn(passwordHash);
        when(customerRepository.findByAccount_Email(request.email())).thenReturn(Optional.empty());
        customerService.createCustomer(request);

        // then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerArgumentCaptor.capture());

        Customer customer = customerArgumentCaptor.getValue();
        verify(customerRepository, times(1)).save(customer);
        assertThat(customer.getId()).isNull();
        assertThat(customer.getAccount().getEmail()).isEqualTo(request.email());
        assertThat(customer.getAccount().getPassword()).isEqualTo(passwordHash);
    }

    @Test
    @DisplayName("Should not create any customer when email is taken")
    void shouldNotCreateCustomerWhenEmailIsTaken() {
        // given
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "david@gmail.com",
                "123456",
                "david",
                "white",
                "123 123 123",
                LocalDate.of(1989, 1, 1),
                CustomerGender.MALE,
                "",
                "Fake AV",
                "50120",
                "USA",
                "123123123Z"
        );

        // when
        when(customerRepository.findByAccount_Email(request.email())).thenReturn(Optional.of(new Customer()));
        CustomerEmailTakenException exception = assertThrows(
                CustomerEmailTakenException.class,
                () -> customerService.createCustomer(request)
        );

        // then
        verify(customerRepository, times(0)).save(any());
        assertEquals(Exceptions.CUSTOMER.EMAIL_TAKEN, exception.getMessage());
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
}
