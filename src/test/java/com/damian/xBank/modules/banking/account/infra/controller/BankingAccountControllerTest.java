package com.damian.xBank.modules.banking.account.infra.controller;

import com.damian.xBank.shared.AbstractIntegrationTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class BankingAccountControllerTest extends AbstractIntegrationTest {
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
    //    void loginWithCustomer(Customer customer) throws Exception {
    //        // given
    //        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
    //                customer.getEmail(), this.rawPassword
    //        );
    //
    //        String jsonRequest = objectMapper.writeValueAsString(authenticationRequest);
    //
    //        // when
    //        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
    //                                          .contentType(MediaType.APPLICATION_JSON)
    //                                          .content(jsonRequest))
    //                                  .andReturn();
    //
    //        AuthenticationResponse response = objectMapper.readValue(
    //                result.getResponse().getContentAsString(),
    //                AuthenticationResponse.class
    //        );
    //
    //        token = response.token();
    //    }
    //
    //    @Test
    //    @DisplayName("Should request a BankingCard")
    //    void shouldRequestBankingCard() throws Exception {
    //        // given
    //        loginWithCustomer(customerA);
    //
    //        BankingAccount bankingAccount = new BankingAccount(customerA);
    //        bankingAccount.setAccountNumber("ES1234567890123456789012");
    //        bankingAccount.setAccountType(BankingAccountType.SAVINGS);
    //        bankingAccount.setAccountCurrency(BankingAccountCurrency.EUR);
    //        bankingAccount.setAccountStatus(BankingAccountStatus.ACTIVE);
    //        bankingAccount.setBalance(BigDecimal.valueOf(1000));
    //        bankingAccountRepository.save(bankingAccount);
    //
    //        BankingCardRequest request = new BankingCardRequest(
    //                BankingCardType.DEBIT
    //        );
    //
    //        // when
    //        MvcResult result = mockMvc
    //                .perform(
    //                        post("/api/v1/customers/me/banking/accounts/{id}/cards/request", bankingAccount.getId())
    //                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
    //                                .contentType(MediaType.APPLICATION_JSON)
    //                                .content(objectMapper.writeValueAsString(request)))
    //                .andDo(print())
    //                .andExpect(status().is(201))
    //                .andReturn();
    //
    //        BankingCardDTO card = objectMapper.readValue(
    //                result.getResponse().getContentAsString(),
    //                BankingCardDTO.class
    //        );
    //
    //        // then
    //        assertThat(card).isNotNull();
    //        assertThat(card.type()).isEqualTo(request.type());
    //    }
    //
    //    @Test
    //    @DisplayName("Should request banking account")
    //    void shouldRequestBankingAccount() throws Exception {
    //        // given
    //        loginWithCustomer(customerA);
    //        BankingAccountCreateRequest request = new BankingAccountCreateRequest(
    //                BankingAccountType.SAVINGS,
    //                BankingAccountCurrency.EUR
    //        );
    //
    //        // when
    //        MvcResult result = mockMvc
    //                .perform(post("/api/v1/customers/me/banking/accounts/request")
    //                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
    //                        .contentType(MediaType.APPLICATION_JSON)
    //                        .content(objectMapper.writeValueAsString(request)))
    //                .andDo(print())
    //                .andExpect(status().is(201))
    //                .andReturn();
    //
    //        BankingAccountDTO bankingAccount = objectMapper.readValue(
    //                result.getResponse().getContentAsString(),
    //                BankingAccountDTO.class
    //        );
    //
    //        // then
    //        assertThat(bankingAccount).isNotNull();
    //        assertThat(bankingAccount.accountNumber()).isNotEmpty();
    //        assertThat(bankingAccount.currency()).isEqualTo(request.currency());
    //        assertThat(bankingAccount.balance()).isEqualTo(BigDecimal.ZERO);
    //        assertThat(bankingAccount.type()).isEqualTo(request.type());
    //    }
    //
    //    @Test
    //    @DisplayName("Should set an alias to your own banking account")
    //    void shouldSetAliasToBankingAccount() throws Exception {
    //        // given
    //        loginWithCustomer(customerA);
    //        BankingAccountAliasUpdateRequest request = new BankingAccountAliasUpdateRequest(
    //                "account for savings",
    //                rawPassword
    //        );
    //
    //        BankingAccount givenBankingAccount = new BankingAccount(customerA);
    //        givenBankingAccount.setAccountNumber("US0011111111222222223333");
    //        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
    //        givenBankingAccount.setAccountCurrency(BankingAccountCurrency.EUR);
    //        bankingAccountRepository.save(givenBankingAccount);
    //
    //        // when
    //        MvcResult result = mockMvc
    //                .perform(patch("/api/v1/customers/me/banking/accounts/{id}/alias", givenBankingAccount.getId())
    //                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
    //                        .contentType(MediaType.APPLICATION_JSON)
    //                        .content(objectMapper.writeValueAsString(request)))
    //                .andDo(print())
    //                .andExpect(status().is(200))
    //                .andReturn();
    //
    //        BankingAccountDTO bankingAccount = objectMapper.readValue(
    //                result.getResponse().getContentAsString(),
    //                BankingAccountDTO.class
    //        );
    //
    //        // then
    //        assertThat(bankingAccount).isNotNull();
    //        assertThat(bankingAccount.alias()).isEqualTo(request.alias());
    //    }


}