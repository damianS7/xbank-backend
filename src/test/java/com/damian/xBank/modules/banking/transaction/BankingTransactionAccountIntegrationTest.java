package com.damian.xBank.modules.banking.transaction;

//import com.damian.xBank.modules.auth.http.AuthenticationRequest;
//import com.damian.xBank.modules.auth.http.AuthenticationResponse;
//import com.damian.xBank.modules.banking.account.*;
//import com.damian.xBank.modules.banking.transactions.BankingTransaction;
//import com.damian.xBank.modules.banking.transactions.BankingTransactionDTO;
//import com.damian.xBank.modules.banking.transactions.BankingTransactionType;
//import com.damian.xBank.modules.banking.transactions.http.BankingAccountTransactionRequest;
//import com.damian.xBank.modules.customer.CustomerRole;
//import com.damian.xBank.modules.user.customer.repository.CustomerRepository;
//import com.damian.xBank.shared.domain.Customer;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ActiveProfiles("test")
//@SpringBootTest
//@AutoConfigureMockMvc
public class BankingTransactionAccountIntegrationTest {
    //    private final String RAW_PASSWORD = "123456";
    //
    //    @Autowired
    //    private MockMvc mockMvc;
    //
    //    @Autowired
    //    private ObjectMapper objectMapper;
    //
    //    @Autowired
    //    private CustomerRepository customerRepository;
    //
    //    @Autowired
    //    private BankingAccountRepository bankingAccountRepository;
    //
    //    @Autowired
    //    private BCryptPasswordEncoder bCryptPasswordEncoder;
    //
    //    @Autowired
    //    private BankingAccountService bankingAccountService;
    //
    //    private Customer customerA;
    //    private Customer customerB;
    //    private Customer customerAdmin;
    //    private String token;
    //
    //    @BeforeEach
    //    void setUp() throws Exception {
    //        customerRepository.deleteAll();
    //        bankingAccountRepository.deleteAll();
    //
    //        customerA = new Customer();
    //        customerA.setEmail("customerA@test.com");
    //        customerA.setPassword(bCryptPasswordEncoder.encode(this.RAW_PASSWORD));
    //        customerA.getProfile().setFirstName("alice");
    //        customerA.getProfile().setLastName("wonderland");
    //        customerA.getProfile().setBirthdate(LocalDate.of(1989, 1, 1));
    //
    //        customerRepository.save(customerA);
    //
    //        customerB = new Customer();
    //        customerB.setEmail("customerB@test.com");
    //        customerB.setPassword(bCryptPasswordEncoder.encode(this.RAW_PASSWORD));
    //        customerB.getProfile().setFirstName("alice");
    //        customerB.getProfile().setLastName("wonderland");
    //        customerB.getProfile().setBirthdate(LocalDate.of(1995, 11, 11));
    //
    //        customerRepository.save(customerB);
    //
    //        customerAdmin = new Customer();
    //        customerAdmin.setEmail("customerC@test.com");
    //        customerAdmin.setRole(CustomerRole.ADMIN);
    //        customerAdmin.setPassword(bCryptPasswordEncoder.encode(this.RAW_PASSWORD));
    //        customerAdmin.getProfile().setFirstName("alice");
    //        customerAdmin.getProfile().setLastName("wonderland");
    //        customerAdmin.getProfile().setBirthdate(LocalDate.of(1995, 11, 11));
    //
    //        customerRepository.save(customerAdmin);
    //    }
}