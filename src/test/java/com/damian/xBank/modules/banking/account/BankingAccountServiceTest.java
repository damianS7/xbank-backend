package com.damian.xBank.modules.banking.account;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BankingAccountServiceTest {

    //    @Mock
    //    private BankingAccountRepository bankingAccountRepository;
    //
    //    @Mock
    //    private CustomerRepository customerRepository;
    //
    //    @Mock
    //    private Faker faker;
    //
    //    @Mock
    //    private Finance finance;
    //
    //    @Mock
    //    private BCryptPasswordEncoder bCryptPasswordEncoder;
    //
    //    @InjectMocks
    //    private BankingAccountService bankingAccountService;
    //
    //    private Customer customerA;
    //    private Customer customerB;
    //    private Customer customerAdmin;
    //
    //    private final String rawPassword = "123456";
    //
    //    @BeforeEach
    //    void setUp() {
    //        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    //        customerRepository.deleteAll();
    //        customerA = new Customer(99L, "customerA@test.com", bCryptPasswordEncoder.encode(rawPassword));
    //        customerB = new Customer(92L, "customerB@test.com", bCryptPasswordEncoder.encode(rawPassword));
    //        customerAdmin = new Customer(95L, "admin@test.com", bCryptPasswordEncoder.encode(rawPassword));
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
    //        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
    //        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(customer);
    //    }
    //
    //    @Test
    //    @DisplayName("Should create a BankingAccount for logged customer")
    //    void shouldCreateBankingAccount() {
    //        // given
    //        Country country = Mockito.mock(Country.class);
    //        Number number = Mockito.mock(Number.class);
    //        setUpContext(customerA);
    //
    //        BankingAccountCreateRequest request = new BankingAccountCreateRequest(
    //                BankingAccountType.SAVINGS,
    //                BankingAccountCurrency.EUR
    //        );
    //
    //        BankingAccount givenBankingAccount = new BankingAccount(customerA);
    //        givenBankingAccount.setAccountNumber("US9900001111112233334444");
    //        givenBankingAccount.setAccountCurrency(request.currency());
    //        givenBankingAccount.setAccountType(request.type());
    //
    //        // when
    //        Mockito.when(faker.country()).thenReturn(country);
    //        Mockito.when(faker.number()).thenReturn(number);
    //        when(faker.country().countryCode2()).thenReturn("US");
    //        when(customerRepository.findById(customerA.getId())).thenReturn(Optional.of(customerA));
    //        when(bankingAccountRepository.save(any(BankingAccount.class))).thenReturn(givenBankingAccount);
    //
    //        BankingAccount savedAccount = bankingAccountService.createBankingAccount(request);
    //
    //        // then
    //        assertThat(savedAccount).isNotNull();
    //        assertThat(savedAccount.getAccountCurrency()).isEqualTo(request.currency());
    //        assertThat(savedAccount.getAccountType()).isEqualTo(request.type());
    //        assertThat(savedAccount.getAccountNumber()).isEqualTo(givenBankingAccount.getAccountNumber());
    //        assertThat(savedAccount.getBalance()).isEqualTo(BigDecimal.valueOf(0));
    //        verify(bankingAccountRepository, times(1)).save(any(BankingAccount.class));
    //    }
    //
    //    @Test
    //    @DisplayName("Should not open a BankingAccount when customer not found")
    //    void shouldNotCreateBankingAccountWhenCustomerNotFound() {
    //        // given
    //        setUpContext(customerA);
    //
    //        BankingAccountCreateRequest request = new BankingAccountCreateRequest(
    //                BankingAccountType.SAVINGS,
    //                BankingAccountCurrency.EUR
    //        );
    //
    //        BankingAccount givenBankingAccount = new BankingAccount(customerA);
    //        givenBankingAccount.setAccountCurrency(request.currency());
    //        givenBankingAccount.setAccountType(request.type());
    //        givenBankingAccount.setAccountNumber("US9900001111112233334444");
    //
    //        // when
    //        when(customerRepository.findById(customerA.getId())).thenReturn(Optional.empty());
    //
    //        CustomerNotFoundException exception = assertThrows(
    //                CustomerNotFoundException.class,
    //                () -> bankingAccountService.createBankingAccount(request)
    //        );
    //
    //        // then
    //        assertTrue(exception.getMessage().contains("Customer not found"));
    //    }
    //
    //    @Test
    //    @DisplayName("Should close a from logged customer BankingAccount")
    //    void shouldCloseBankingAccountFromLoggedCustomer() {
    //        // given
    //        setUpContext(customerA);
    //        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
    //                rawPassword
    //        );
    //
    //        BankingAccount givenBankingAccount = new BankingAccount(customerA);
    //        givenBankingAccount.setId(5L);
    //        givenBankingAccount.setAccountCurrency(BankingAccountCurrency.EUR);
    //        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
    //        givenBankingAccount.setAccountNumber("US9900001111112233334444");
    //
    //        // when
    //        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(givenBankingAccount));
    //        when(bankingAccountRepository.save(any(BankingAccount.class))).thenReturn(givenBankingAccount);
    //
    //        BankingAccount savedAccount = bankingAccountService.closeBankingAccount(
    //                givenBankingAccount.getId(),
    //                request
    //        );
    //
    //        // then
    //        assertThat(savedAccount.getAccountStatus()).isEqualTo(BankingAccountStatus.CLOSED);
    //        verify(bankingAccountRepository, times(1)).save(any(BankingAccount.class));
    //    }
    //
    //    @Test
    //    @DisplayName("Should not close BankingAccount When is suspended")
    //    void shouldNotCloseBankingAccountWhenSuspended() {
    //        // given
    //        setUpContext(customerA);
    //
    //        final String accountNumber = "US99 0000 1111 1122 3333 4444";
    //        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
    //                rawPassword
    //        );
    //
    //        BankingAccount givenBankingAccount = new BankingAccount(customerA);
    //        givenBankingAccount.setAccountStatus(BankingAccountStatus.SUSPENDED);
    //        givenBankingAccount.setAccountCurrency(BankingAccountCurrency.EUR);
    //        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
    //        givenBankingAccount.setAccountNumber(accountNumber);
    //
    //        // when
    //        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(givenBankingAccount));
    //
    //        BankingAccountAuthorizationException exception = assertThrows(
    //                BankingAccountAuthorizationException.class,
    //                () -> bankingAccountService.closeBankingAccount(givenBankingAccount.getId(), request)
    //        );
    //
    //        // then
    //        assertTrue(exception.getMessage().contains("suspended"));
    //    }
    //
    //    @Test
    //    @DisplayName("Should not close BankingAccount When none is found")
    //    void shouldNotCloseBankingAccountWhenNotFound() {
    //        // given
    //        setUpContext(customerA);
    //
    //        final String accountNumber = "US99 0000 1111 1122 3333 4444";
    //        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
    //                rawPassword
    //        );
    //
    //        BankingAccount givenBankingAccount = new BankingAccount(customerA);
    //        givenBankingAccount.setAccountCurrency(BankingAccountCurrency.EUR);
    //        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
    //        givenBankingAccount.setAccountNumber(accountNumber);
    //
    //        // when
    //        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.empty());
    //
    //        BankingAccountNotFoundException exception = assertThrows(
    //                BankingAccountNotFoundException.class,
    //                () -> bankingAccountService.closeBankingAccount(givenBankingAccount.getId(), request)
    //        );
    //
    //        // then
    //        assertTrue(exception.getMessage().contains(
    //                Exceptions.USER_ACCOUNT.NOT_FOUND
    //        ));
    //    }
    //
    //    @Test
    //    @DisplayName("Should not close account if you are not the owner and you are not admin either")
    //    void shouldNotCloseBankingAccountWhenItsNotYoursAndYouAreNotAdmin() {
    //        // given
    //        setUpContext(customerA);
    //        BankingAccountCloseRequest request = new BankingAccountCloseRequest(
    //                rawPassword
    //        );
    //
    //        final String accountNumber = "US99 0000 1111 1122 3333 4444";
    //
    //        BankingAccount givenBankingAccount = new BankingAccount(customerB);
    //        givenBankingAccount.setId(5L);
    //        givenBankingAccount.setAccountCurrency(BankingAccountCurrency.EUR);
    //        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
    //        givenBankingAccount.setAccountNumber(accountNumber);
    //
    //        // when
    //        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(givenBankingAccount));
    //
    //        BankingAccountAuthorizationException exception = assertThrows(
    //                BankingAccountAuthorizationException.class,
    //                () -> bankingAccountService.closeBankingAccount(givenBankingAccount.getId(), request)
    //        );
    //
    //        // then
    //        assertTrue(exception.getMessage().contains(
    //                Exceptions.USER_ACCOUNT.ACCESS_FORBIDDEN
    //        ));
    //    }
    //
    //    @Test
    //    @DisplayName("Should close an account even if its not yours when you are ADMIN")
    //    void shouldCloseBankingAccountWhenYouAreAdmin() {
    //        // given
    //        //        setUpContext(customerAdmin);
    //
    //        final String accountNumber = "US99 0000 1111 1122 3333 4444";
    //
    //        BankingAccount givenBankingAccount = new BankingAccount(customerA);
    //        givenBankingAccount.setId(5L);
    //        givenBankingAccount.setAccountCurrency(BankingAccountCurrency.EUR);
    //        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
    //        givenBankingAccount.setAccountNumber(accountNumber);
    //
    //        // when
    //        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(givenBankingAccount));
    //        when(bankingAccountRepository.save(any(BankingAccount.class))).thenReturn(givenBankingAccount);
    //
    //        BankingAccount savedAccount = bankingAccountService.closeBankingAccount(
    //                givenBankingAccount.getId()
    //        );
    //
    //        // then
    //        assertThat(savedAccount.getAccountStatus()).isEqualTo(BankingAccountStatus.CLOSED);
    //        verify(bankingAccountRepository, times(1)).save(any(BankingAccount.class));
    //    }
    //
    //    @Test
    //    @DisplayName("Should get a customer with its banking account data")
    //    void shouldGetCustomerBankingAccountsFromCustomer() {
    //        // given
    //        Set<BankingAccount> bankingAccounts = new HashSet<>();
    //        BankingAccount bankingAccountA = new BankingAccount(customerA);
    //        bankingAccountA.setAccountCurrency(BankingAccountCurrency.EUR);
    //        bankingAccountA.setAccountType(BankingAccountType.SAVINGS);
    //        bankingAccountA.setAccountNumber("US99 0000 1111 1122 3333 4444");
    //        bankingAccounts.add(bankingAccountA);
    //
    //        BankingAccount bankingAccountB = new BankingAccount(customerA);
    //        bankingAccountB.setAccountCurrency(BankingAccountCurrency.EUR);
    //        bankingAccountB.setAccountType(BankingAccountType.SAVINGS);
    //        bankingAccountB.setAccountNumber("US99 0000 1111 1122 3333 6666");
    //        bankingAccounts.add(bankingAccountB);
    //
    //        // when
    //        when(bankingAccountRepository.findByCustomer_Id(anyLong())).thenReturn(bankingAccounts);
    //
    //        Set<BankingAccount> result = bankingAccountService.getCustomerBankingAccounts(customerA.getId());
    //
    //        // then
    //        assertThat(result.size()).isEqualTo(2);
    //        verify(bankingAccountRepository, times(1)).findByCustomer_Id(anyLong());
    //    }
    //
    //    @Test
    //    @DisplayName("Should set alias to a logged customer BankingAccount")
    //    void shouldSetAliasToBankingAccountFromLoggedCustomer() {
    //        // given
    //        setUpContext(customerA);
    //        BankingAccountAliasUpdateRequest request = new BankingAccountAliasUpdateRequest(
    //                "account for savings",
    //                rawPassword
    //        );
    //
    //        BankingAccount givenBankingAccount = new BankingAccount(customerA);
    //        givenBankingAccount.setId(5L);
    //        givenBankingAccount.setAccountCurrency(BankingAccountCurrency.EUR);
    //        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
    //        givenBankingAccount.setAccountNumber("US9900001111112233334444");
    //
    //        // when
    //        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(givenBankingAccount));
    //        when(bankingAccountRepository.save(any(BankingAccount.class))).thenReturn(givenBankingAccount);
    //
    //        BankingAccount savedAccount = bankingAccountService.setBankingAccountAlias(
    //                givenBankingAccount.getId(),
    //                request
    //        );
    //
    //        // then
    //        assertThat(savedAccount.getAlias()).isEqualTo(request.alias());
    //        verify(bankingAccountRepository, times(1)).save(any(BankingAccount.class));
    //    }
    //
    //    @Test
    //    @DisplayName("Should not open BankingAccount When is closed")
    //    void shouldNotOpenBankingAccountWhenIsClosed() {
    //        // given
    //        setUpContext(customerA);
    //
    //        final String accountNumber = "US99 0000 1111 1122 3333 4444";
    //        BankingAccountOpenRequest request = new BankingAccountOpenRequest(
    //                rawPassword
    //        );
    //
    //        BankingAccount givenBankingAccount = new BankingAccount(customerA);
    //        givenBankingAccount.setAccountStatus(BankingAccountStatus.CLOSED);
    //        givenBankingAccount.setAccountCurrency(BankingAccountCurrency.EUR);
    //        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
    //        givenBankingAccount.setAccountNumber(accountNumber);
    //
    //        // when
    //        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(givenBankingAccount));
    //
    //        BankingAccountAuthorizationException exception = assertThrows(
    //                BankingAccountAuthorizationException.class,
    //                () -> bankingAccountService.openBankingAccount(givenBankingAccount.getId(), request)
    //        );
    //
    //        // then
    //        assertEquals(Exceptions.USER_ACCOUNT.CLOSED, exception.getMessage());
    //    }
    //
    //    @Test
    //    @DisplayName("Should not open BankingAccount When is suspended")
    //    void shouldNotOpenBankingAccountWhenSuspended() {
    //        // given
    //        setUpContext(customerA);
    //
    //        final String accountNumber = "US99 0000 1111 1122 3333 4444";
    //        BankingAccountOpenRequest request = new BankingAccountOpenRequest(
    //                rawPassword
    //        );
    //
    //        BankingAccount givenBankingAccount = new BankingAccount(customerA);
    //        givenBankingAccount.setAccountStatus(BankingAccountStatus.SUSPENDED);
    //        givenBankingAccount.setAccountCurrency(BankingAccountCurrency.EUR);
    //        givenBankingAccount.setAccountType(BankingAccountType.SAVINGS);
    //        givenBankingAccount.setAccountNumber(accountNumber);
    //
    //        // when
    //        when(bankingAccountRepository.findById(givenBankingAccount.getId())).thenReturn(Optional.of(givenBankingAccount));
    //
    //        BankingAccountAuthorizationException exception = assertThrows(
    //                BankingAccountAuthorizationException.class,
    //                () -> bankingAccountService.openBankingAccount(givenBankingAccount.getId(), request)
    //        );
    //
    //        // then
    //        assertEquals(Exceptions.USER_ACCOUNT.SUSPENDED, exception.getMessage());
    //    }
}
