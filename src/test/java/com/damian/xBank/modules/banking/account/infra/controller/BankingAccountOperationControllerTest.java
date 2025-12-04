package com.damian.xBank.modules.banking.account.infra.controller;

import com.damian.xBank.shared.AbstractIntegrationTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class BankingAccountOperationControllerTest extends AbstractIntegrationTest {
    //    private final String rawPassword = "123456";
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
    //
    //        customerA = new Customer();
    //        customerA.setEmail("customerA@test.com");
    //        customerA.setPassword(bCryptPasswordEncoder.encode(this.rawPassword));
    //        customerA.getProfile().setFirstName("alice");
    //        customerA.getProfile().setLastName("wonderland");
    //        customerA.getProfile().setBirthdate(LocalDate.of(1989, 1, 1));
    //
    //        customerRepository.save(customerA);
    //
    //        customerB = new Customer();
    //        customerB.setEmail("customerB@test.com");
    //        customerB.setPassword(bCryptPasswordEncoder.encode(this.rawPassword));
    //        customerB.getProfile().setFirstName("alice");
    //        customerB.getProfile().setLastName("wonderland");
    //        customerB.getProfile().setBirthdate(LocalDate.of(1995, 11, 11));
    //
    //        customerRepository.save(customerB);
    //
    //        customerAdmin = new Customer();
    //        customerAdmin.setEmail("customerC@test.com");
    //        customerAdmin.setRole(CustomerRole.ADMIN);
    //        customerAdmin.setPassword(bCryptPasswordEncoder.encode(this.rawPassword));
    //        customerAdmin.getProfile().setFirstName("alice");
    //        customerAdmin.getProfile().setLastName("wonderland");
    //        customerAdmin.getProfile().setBirthdate(LocalDate.of(1995, 11, 11));
    //
    //        customerRepository.save(customerAdmin);
    //    }
    //
    //

    //
    //    @Test
    //    @DisplayName("Should transfer to another customer")
    //    void shouldTransferToAnotherCustomer() throws Exception {
    //        // given
    //        loginWithCustomer(customerA);
    //
    //        BigDecimal givenTransferAmount = BigDecimal.valueOf(100);
    //
    //        BankingAccount bankingAccountA = new BankingAccount(customerA);
    //        bankingAccountA.setAccountNumber("ES1234567890123456789012");
    //        bankingAccountA.setAccountType(BankingAccountType.SAVINGS);
    //        bankingAccountA.setAccountCurrency(BankingAccountCurrency.EUR);
    //        bankingAccountA.setAccountStatus(BankingAccountStatus.ACTIVE);
    //        bankingAccountA.setBalance(BigDecimal.valueOf(3200));
    //        bankingAccountRepository.save(bankingAccountA);
    //
    //        BankingAccount bankingAccountB = new BankingAccount(customerB);
    //        bankingAccountB.setAccountNumber("DE1234567890123456789012");
    //        bankingAccountB.setAccountType(BankingAccountType.SAVINGS);
    //        bankingAccountB.setAccountCurrency(BankingAccountCurrency.EUR);
    //        bankingAccountB.setAccountStatus(BankingAccountStatus.ACTIVE);
    //        bankingAccountB.setBalance(BigDecimal.valueOf(200));
    //        bankingAccountRepository.save(bankingAccountB);
    //
    //        BankingAccountTransactionRequest request = new BankingAccountTransactionRequest(
    //                bankingAccountB.getAccountNumber(),
    //                BankingTransactionType.TRANSFER_TO,
    //                "Enjoy!",
    //                givenTransferAmount,
    //                RAW_PASSWORD
    //        );
    //
    //        // when
    //        MvcResult result = mockMvc
    //                .perform(post(
    //                        "/api/v1/customers/me/banking/accounts/" + bankingAccountA.getId() + "/transactions")
    //                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
    //                        .contentType(MediaType.APPLICATION_JSON)
    //                        .content(objectMapper.writeValueAsString(request)))
    //                .andDo(print())
    //                .andExpect(status().is(201))
    //                .andReturn();
    //
    //        BankingTransactionDTO transaction = objectMapper.readValue(
    //                result.getResponse().getContentAsString(),
    //                BankingTransactionDTO.class
    //        );
    //
    //        BankingAccount updatedBankingAccountA = bankingAccountRepository.findById(bankingAccountA.getId()).get();
    //
    //        // then
    //        assertThat(transaction).isNotNull();
    //        assertEquals(
    //                updatedBankingAccountA.getBalance(),
    //                bankingAccountA.getBalance().subtract(givenTransferAmount).setScale(2)
    //        );
    //        assertEquals(transaction.transactionType(), BankingTransactionType.TRANSFER_TO);
    //        assertEquals(transaction.amount(), givenTransferAmount);
    //        assertEquals(transaction.description(), "Enjoy!");
    //        assertEquals(transaction.accountId(), bankingAccountA.getId());
    //    }
    //
    //    @Test
    //    @DisplayName("Should not transfer to same account number")
    //    void shouldNotTransferToSameAccount() throws Exception {
    //        // given
    //        loginWithCustomer(customerA);
    //
    //        BigDecimal givenTransferAmount = BigDecimal.valueOf(100);
    //
    //        BankingAccount bankingAccountA = new BankingAccount(customerA);
    //        bankingAccountA.setAccountNumber("ES1234567890123456789012");
    //        bankingAccountA.setAccountType(BankingAccountType.SAVINGS);
    //        bankingAccountA.setAccountCurrency(BankingAccountCurrency.EUR);
    //        bankingAccountA.setAccountStatus(BankingAccountStatus.ACTIVE);
    //        bankingAccountA.setBalance(BigDecimal.valueOf(3200));
    //        bankingAccountRepository.save(bankingAccountA);
    //
    //        BankingAccount bankingAccountB = new BankingAccount(customerB);
    //        bankingAccountB.setAccountNumber("DE1234567890123456789012");
    //        bankingAccountB.setAccountType(BankingAccountType.SAVINGS);
    //        bankingAccountB.setAccountCurrency(BankingAccountCurrency.EUR);
    //        bankingAccountB.setAccountStatus(BankingAccountStatus.ACTIVE);
    //        bankingAccountB.setBalance(BigDecimal.valueOf(200));
    //        bankingAccountRepository.save(bankingAccountB);
    //
    //        BankingAccountTransactionRequest request = new BankingAccountTransactionRequest(
    //                bankingAccountA.getAccountNumber(),
    //                BankingTransactionType.TRANSFER_TO,
    //                "Enjoy!",
    //                givenTransferAmount,
    //                RAW_PASSWORD
    //        );
    //
    //        // when
    //        MvcResult result = mockMvc
    //                .perform(post(
    //                        "/api/v1/customers/me/banking/accounts/" + bankingAccountA.getId() + "/transactions")
    //                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
    //                        .contentType(MediaType.APPLICATION_JSON)
    //                        .content(objectMapper.writeValueAsString(request)))
    //                .andDo(print())
    //                .andExpect(status().is(403))
    //                .andReturn();
    //
    //        // then
    //    }
    //
    //    @Test
    //    @DisplayName("Should not transfer when account is closed")
    //    void shouldNotTransferWhenAccountIsClosed() throws Exception {
    //        // given
    //        loginWithCustomer(customerA);
    //        BigDecimal givenTransferAmount = BigDecimal.valueOf(100);
    //
    //        BankingAccount bankingAccountA = new BankingAccount(customerA);
    //        bankingAccountA.setAccountNumber("ES1234567890123456789012");
    //        bankingAccountA.setAccountStatus(BankingAccountStatus.CLOSED);
    //        bankingAccountA.setBalance(BigDecimal.valueOf(3200));
    //        bankingAccountRepository.save(bankingAccountA);
    //
    //        BankingAccount bankingAccountB = new BankingAccount(customerB);
    //        bankingAccountB.setAccountNumber("DE1234567890123456789012");
    //        bankingAccountB.setAccountStatus(BankingAccountStatus.ACTIVE);
    //        bankingAccountB.setBalance(BigDecimal.valueOf(200));
    //        bankingAccountRepository.save(bankingAccountB);
    //
    //        BankingAccountTransactionRequest request = new BankingAccountTransactionRequest(
    //                bankingAccountB.getAccountNumber(),
    //                BankingTransactionType.TRANSFER_TO,
    //                "Enjoy!",
    //                givenTransferAmount,
    //                RAW_PASSWORD
    //        );
    //
    //
    //        BankingTransaction transaction = new BankingTransaction(bankingAccountA);
    //        transaction.setTransactionType(request.transactionType());
    //        transaction.setDescription(request.description());
    //        transaction.setAmount(request.amount());
    //
    //        // when
    //        mockMvc.perform(post("/api/v1/customers/me/banking/accounts/{id}/transactions", bankingAccountA.getId())
    //                       .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
    //                       .contentType(MediaType.APPLICATION_JSON)
    //                       .content(objectMapper.writeValueAsString(request)))
    //               .andDo(print())
    //               .andExpect(status().is(403));
    //
    //        // then
    //    }
    //
    //    @Test
    //    @DisplayName("Should not transfer to when insufficient funds")
    //    void shouldTransferToWhenInsufficientFunds() throws Exception {
    //        // given
    //        loginWithCustomer(customerA);
    //
    //        BigDecimal givenTransferAmount = BigDecimal.valueOf(100);
    //
    //        BankingAccount bankingAccountA = new BankingAccount(customerA);
    //        bankingAccountA.setAccountNumber("ES1234567890123456789012");
    //        bankingAccountA.setAccountType(BankingAccountType.SAVINGS);
    //        bankingAccountA.setAccountCurrency(BankingAccountCurrency.EUR);
    //        bankingAccountA.setAccountStatus(BankingAccountStatus.ACTIVE);
    //        bankingAccountA.setBalance(BigDecimal.valueOf(0));
    //        bankingAccountRepository.save(bankingAccountA);
    //
    //        BankingAccount bankingAccountB = new BankingAccount(customerB);
    //        bankingAccountB.setAccountNumber("DE1234567890123456789012");
    //        bankingAccountB.setAccountType(BankingAccountType.SAVINGS);
    //        bankingAccountB.setAccountCurrency(BankingAccountCurrency.EUR);
    //        bankingAccountB.setAccountStatus(BankingAccountStatus.ACTIVE);
    //        bankingAccountB.setBalance(BigDecimal.valueOf(200));
    //        bankingAccountRepository.save(bankingAccountB);
    //
    //        BankingAccountTransactionRequest request = new BankingAccountTransactionRequest(
    //                bankingAccountB.getAccountNumber(),
    //                BankingTransactionType.TRANSFER_TO,
    //                "Enjoy!",
    //                givenTransferAmount,
    //                RAW_PASSWORD
    //        );
    //
    //        // when
    //        MvcResult result = mockMvc
    //                .perform(post(
    //                        "/api/v1/customers/me/banking/accounts/" + bankingAccountA.getId() + "/transactions")
    //                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
    //                        .contentType(MediaType.APPLICATION_JSON)
    //                        .content(objectMapper.writeValueAsString(request)))
    //                .andDo(print())
    //                .andExpect(status().is(403))
    //                .andReturn();
    //    }
}