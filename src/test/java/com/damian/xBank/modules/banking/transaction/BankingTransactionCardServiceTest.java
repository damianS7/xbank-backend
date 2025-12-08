package com.damian.xBank.modules.banking.transaction;

//import com.damian.xBank.modules.banking.account.BankingAccount;
//import com.damian.xBank.modules.banking.card.BankingCard;
//import com.damian.xBank.modules.banking.card.BankingCardLockStatus;
//import com.damian.xBank.modules.banking.card.BankingCardRepository;
//import com.damian.xBank.modules.banking.card.BankingCardStatus;
//import com.damian.xBank.modules.banking.card.exception.BankingCardAuthorizationException;
//import com.damian.xBank.modules.banking.card.exception.BankingCardNotFoundException;
//import com.damian.xBank.modules.banking.transactions.*;
//import com.damian.xBank.modules.banking.transactions.http.BankingCardTransactionRequest;
//import com.damian.xBank.modules.customer.CustomerRole;
//import com.damian.xBank.modules.user.customer.repository.CustomerRepository;
//import com.damian.xBank.shared.domain.Customer;
//import com.damian.xBank.shared.exception.Exceptions;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.when;

//@ExtendWith(MockitoExtension.class)
public class BankingTransactionCardServiceTest {
    //
    //    @Mock
    //    private CustomerRepository customerRepository;
    //
    //    @Mock
    //    private BankingCardRepository bankingCardRepository;
    //
    //    @Mock
    //    private BankingTransactionService bankingTransactionService;
    //
    //    @InjectMocks
    //    private BankingTransactionCardService bankingTransactionCardService;
    //
    //    private Customer customerA;
    //    private Customer customerB;
    //    private Customer customerAdmin;
    //
    //    private final String RAW_PASSWORD = "123456";
    //
    //    @BeforeEach
    //    void setUp() {
    //        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    //        customerRepository.deleteAll();
    //        customerA = new Customer(99L, "customerA@test.com", bCryptPasswordEncoder.encode(RAW_PASSWORD));
    //        customerB = new Customer(92L, "customerB@test.com", bCryptPasswordEncoder.encode(RAW_PASSWORD));
    //        customerAdmin = new Customer(95L, "admin@test.com", bCryptPasswordEncoder.encode(RAW_PASSWORD));
    //        customerAdmin.setRole(CustomerRole.ADMIN);
    //    }
    //
    //    @AfterEach
    //    public void tearDown() {
    //        SecurityContextHolder.clearContext();
    //    }
    //
    //    void setUpContext(Customer customer) {
    //        Authentication authentication = Mockito.mock(Authentication.class);
    //        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    //        SecurityContextHolder.setContext(securityContext);
    //        Mockito.when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
    //        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(customer);
    //    }
    //


}
