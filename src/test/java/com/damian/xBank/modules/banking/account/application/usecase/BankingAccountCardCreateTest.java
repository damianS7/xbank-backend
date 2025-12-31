package com.damian.xBank.modules.banking.account.application.usecase;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCardRequest;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotOwnerException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.card.application.service.BankingCardService;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardType;
import com.damian.xBank.modules.banking.card.domain.exception.BankingAccountCardsLimitException;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.ErrorCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class BankingAccountCardCreateTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingCardService bankingCardService;

    @InjectMocks
    private BankingAccountCardCreate bankingAccountCardCreate;

    private Customer customer;
    private BankingAccount bankingAccount;

    @BeforeEach
    void setUp() {
        customer = Customer.create(
                UserAccount.create()
                           .setId(1L)
                           .setEmail("fromCustomer@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(1L);

        bankingAccount = BankingAccount
                .create(customer)
                .setId(1L)
                .setBalance(BigDecimal.valueOf(1000))
                .setCurrency(BankingAccountCurrency.EUR)
                .setType(BankingAccountType.SAVINGS)
                .setAccountNumber("US9900001111112233334444");

        customer.addBankingAccount(bankingAccount);
    }

    @Test
    @DisplayName("Should returns a BankingCard linked to the account when request is valid")
    void execute_WhenValidRequest_ReturnsBankingCard() {
        // given
        setUpContext(customer);

        BankingCard givenBankingCard = BankingCard
                .create(bankingAccount)
                .setId(11L)
                .setCardNumber("1234567890123456");

        BankingAccountCardRequest request = new BankingAccountCardRequest(BankingCardType.CREDIT);

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.of(bankingAccount));

        when(bankingCardService
                .createBankingCard(any(BankingAccount.class), any(BankingCardType.class)))
                .thenReturn(givenBankingCard);

        BankingCard result = bankingAccountCardCreate.execute(
                bankingAccount.getId(),
                request
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCardNumber()).isEqualTo(givenBankingCard.getCardNumber());
        assertThat(result.getCardType()).isEqualTo(givenBankingCard.getCardType());
    }

    @Test
    @DisplayName("Should throws exception when banking account not found")
    void execute_WhenAccountNotFound_ThrowsException() {
        // given
        setUpContext(customer);

        BankingAccountCardRequest request = new BankingAccountCardRequest(BankingCardType.CREDIT);

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.empty());

        BankingAccountNotFoundException exception = assertThrows(
                BankingAccountNotFoundException.class,
                () -> bankingAccountCardCreate.execute(
                        bankingAccount.getId(),
                        request
                )
        );

        // then
        assertEquals(ErrorCodes.BANKING_ACCOUNT_NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should throws exception when authenticated customer is not the owner of the account")
    void execute_WhenAccountNotOwnedByCustomer_ThrowsException() {
        // given
        UserAccount userAccountB = UserAccount.create()
                                              .setId(2L)
                                              .setEmail("customer@demo.com")
                                              .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD));

        Customer customerB = Customer.create()
                                     .setId(2L)
                                     .setAccount(userAccountB);

        setUpContext(customerB);

        BankingAccountCardRequest request = new BankingAccountCardRequest(BankingCardType.CREDIT);

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.of(bankingAccount));

        BankingAccountNotOwnerException exception = assertThrows(
                BankingAccountNotOwnerException.class,
                () -> bankingAccountCardCreate.execute(
                        99L,
                        request
                )
        );

        // then
        assertEquals(ErrorCodes.BANKING_ACCOUNT_NOT_OWNER, exception.getMessage());
    }

    @Test
    @DisplayName("Should returns a BankingCard when account is not yours but you are admin")
    void execute_WhenAccountNotOwnedByCustomerButItIsAdmin_ReturnsBankingCard() {
        // given
        UserAccount adminAccount = UserAccount.create()
                                              .setId(1L)
                                              .setRole(UserAccountRole.ADMIN)
                                              .setEmail("customer@demo.com")
                                              .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD));

        Customer admin = Customer.create()
                                 .setId(1L)
                                 .setAccount(adminAccount);

        setUpContext(admin);

        BankingCard givenBankingCard = BankingCard
                .create(bankingAccount)
                .setId(11L)
                .setCardNumber("1234567890123456");

        BankingAccountCardRequest request = new BankingAccountCardRequest(BankingCardType.CREDIT);

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.of(bankingAccount));

        when(bankingCardService.createBankingCard(any(BankingAccount.class), any(BankingCardType.class)))
                .thenReturn(givenBankingCard);

        BankingCard result = bankingAccountCardCreate.execute(
                bankingAccount.getId(),
                request
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCardNumber()).isEqualTo(givenBankingCard.getCardNumber());
        assertThat(result.getCardType()).isEqualTo(givenBankingCard.getCardType());
    }

    @Test
    @DisplayName("Should throws exception when cards per account reached limit")
    void execute_WhenCardLimitPerAccountReached_ThrowsException() {
        // given
        setUpContext(customer);

        for (int i = 0; i < bankingAccount.getCardLimit(); i++) {
            bankingAccount.addBankingCard(
                    BankingCard.create(bankingAccount).setCardStatus(BankingCardStatus.ACTIVE)
            );
        }

        BankingAccountCardRequest request = new BankingAccountCardRequest(BankingCardType.CREDIT);

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.of(bankingAccount));

        BankingAccountCardsLimitException exception = assertThrows(
                BankingAccountCardsLimitException.class,
                () -> bankingAccountCardCreate.execute(
                        bankingAccount.getId(),
                        request
                )
        );

        // then
        assertEquals(ErrorCodes.BANKING_ACCOUNT_CARD_LIMIT, exception.getMessage());
    }
}