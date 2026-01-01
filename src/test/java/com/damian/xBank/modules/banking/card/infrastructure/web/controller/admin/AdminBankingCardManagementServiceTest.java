package com.damian.xBank.modules.banking.card.infrastructure.web.controller.admin;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

public class AdminBankingCardManagementServiceTest extends AbstractServiceTest {
    @Mock
    private BankingCardRepository bankingCardRepository;

    //    @InjectMocks
    //    private AdminBankingCardManagementService adminBankingCardManagementService;

    private Customer customer;
    private BankingAccount customerBankingAccount;
    private BankingCard customerBankingCard;

    @BeforeEach
    void setUp() {
        customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("customer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        customerBankingAccount = BankingAccount
                .create(customer)
                .setId(5L)
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");


        customerBankingCard = BankingCard
                .create(customerBankingAccount)
                .setId(11L)
                .setCardNumber("1234123412341234")
                .setCardCvv("123")
                .setCardPin("1234");
    }

    //
    //    @Test
    //    @DisplayName("Should disable a card when logged as admin")
    //    void shouldDisableCardWhenAdmin() throws Exception {
    //        // given
    //        customer.setRole(UserAccountRole.ADMIN);
    //        customerRepository.save(customer);
    //
    //        login(customer);
    //
    //        BankingCardUpdateStatusRequest request = new BankingCardUpdateStatusRequest(
    //                BankingCardStatus.DISABLED
    //        );
    //
    //        // when
    //        MvcResult result = mockMvc
    //                .perform(patch("/api/v1/admin/banking/cards/{id}/status", customerBankingCard.getId())
    //                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
    //                        .contentType(MediaType.APPLICATION_JSON)
    //                        .content(objectMapper.writeValueAsString(request)))
    //                .andDo(print())
    //                .andExpect(status().is(200))
    //                .andReturn();
    //
    //        BankingCardDto card = objectMapper.readValue(
    //                result.getResponse().getContentAsString(),
    //                BankingCardDto.class
    //        );
    //
    //        // then
    //        assertThat(card).isNotNull();
    //        assertThat(card.cardStatus()).isEqualTo(BankingCardStatus.DISABLED);
    //    }
    //
    //    @Test
    //    @DisplayName("Should fail to disable a card when not admin")
    //    void shouldFailToDisableCardWhenNotAdmin() throws Exception {
    //        // given
    //        customer.setRole(UserAccountRole.CUSTOMER);
    //        customerRepository.save(customer);
    //
    //        login(customer);
    //
    //        BankingCardUpdateStatusRequest request = new BankingCardUpdateStatusRequest(
    //                BankingCardStatus.DISABLED
    //        );
    //
    //        // when
    //        mockMvc
    //                .perform(patch("/api/v1/admin/banking/cards/{id}/status", customerBankingCard.getId())
    //                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
    //                        .contentType(MediaType.APPLICATION_JSON)
    //                        .content(objectMapper.writeValueAsString(request)))
    //                .andDo(print())
    //                .andExpect(status().is(403));
    //    }
    //
    //    @Test
    //    @DisplayName("Should disable a card")
    //    void shouldUpdateStatus() {
    //        // given
    //        BankingCardUpdateStatusRequest request = new BankingCardUpdateStatusRequest(
    //                BankingCardStatus.DISABLED
    //        );
    //
    //        // when
    //        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(customerBankingCard));
    //        when(bankingCardRepository.save(any(BankingCard.class)))
    //                .thenAnswer(invocation -> invocation.getArgument(0));
    //
    //        BankingCard cancelledCard = adminBankingCardManagementService.updateStatus(
    //                customerBankingCard.getId(), request
    //        );
    //
    //        // then
    //        assertThat(cancelledCard.getCardNumber()).isEqualTo(customerBankingCard.getCardNumber());
    //        assertThat(cancelledCard.getCardType()).isEqualTo(customerBankingCard.getCardType());
    //        assertThat(cancelledCard.getStatus()).isEqualTo(BankingCardStatus.DISABLED);
    //        verify(bankingCardRepository, times(1)).save(any(BankingCard.class));
    //    }
    //
    //    @Test
    //    @DisplayName("Should fail to disable card when not exists")
    //    void shouldFailToUpdateStatusWhenNotExists() {
    //        // given
    //        BankingCardUpdateStatusRequest request = new BankingCardUpdateStatusRequest(
    //                BankingCardStatus.DISABLED
    //        );
    //
    //        // when
    //        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.empty());
    //
    //        assertThrows(
    //                BankingCardNotFoundException.class,
    //                () -> adminBankingCardManagementService.updateStatus(
    //                        customerBankingCard.getId(), request)
    //        );
    //
    //        // then
    //        verify(bankingCardRepository, times(0)).save(any(BankingCard.class));
    //    }
}
