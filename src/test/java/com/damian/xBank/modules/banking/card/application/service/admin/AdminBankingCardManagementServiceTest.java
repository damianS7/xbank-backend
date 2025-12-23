package com.damian.xBank.modules.banking.card.application.service.admin;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdateStatusRequest;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class AdminBankingCardManagementServiceTest extends AbstractServiceTest {
    @Mock
    private BankingCardRepository bankingCardRepository;

    @InjectMocks
    private AdminBankingCardManagementService adminBankingCardManagementService;

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
                .create()
                .setOwner(customer)
                .setId(5L)
                .setAccountCurrency(BankingAccountCurrency.EUR)
                .setAccountType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");


        customerBankingCard = BankingCard
                .create()
                .setId(11L)
                .setAssociatedBankingAccount(customerBankingAccount)
                .setCardNumber("1234123412341234")
                .setCardCvv("123")
                .setCardPin("1234");
    }

    @Test
    @DisplayName("Should disable a card")
    void shouldUpdateStatus() {
        // given
        BankingCardUpdateStatusRequest request = new BankingCardUpdateStatusRequest(
                BankingCardStatus.DISABLED
        );

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(customerBankingCard));
        when(bankingCardRepository.save(any(BankingCard.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BankingCard cancelledCard = adminBankingCardManagementService.updateStatus(
                customerBankingCard.getId(), request
        );

        // then
        assertThat(cancelledCard.getCardNumber()).isEqualTo(customerBankingCard.getCardNumber());
        assertThat(cancelledCard.getCardType()).isEqualTo(customerBankingCard.getCardType());
        assertThat(cancelledCard.getStatus()).isEqualTo(BankingCardStatus.DISABLED);
        verify(bankingCardRepository, times(1)).save(any(BankingCard.class));
    }


    @Test
    @DisplayName("Should fail to disable card when not exists")
    void shouldFailToUpdateStatusWhenNotExists() {
        // given
        BankingCardUpdateStatusRequest request = new BankingCardUpdateStatusRequest(
                BankingCardStatus.DISABLED
        );

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                BankingCardNotFoundException.class,
                () -> adminBankingCardManagementService.updateStatus(
                        customerBankingCard.getId(), request)
        );

        // then
        verify(bankingCardRepository, times(0)).save(any(BankingCard.class));
    }
}
