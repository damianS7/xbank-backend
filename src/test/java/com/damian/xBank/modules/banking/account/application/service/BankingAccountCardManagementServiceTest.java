package com.damian.xBank.modules.banking.account.application.service;

import com.damian.xBank.modules.banking.account.application.dto.request.BankingAccountCardRequest;
import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountOwnershipException;
import com.damian.xBank.modules.banking.account.infra.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.card.application.service.BankingCardService;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardType;
import com.damian.xBank.modules.banking.card.domain.exception.BankingAccountCardsLimitException;
import com.damian.xBank.modules.banking.card.infra.repository.BankingCardRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.Exceptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class BankingAccountCardManagementServiceTest extends AbstractServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private BankingCardRepository bankingCardRepository;

    @Mock
    private BankingCardService bankingCardService;

    @InjectMocks
    private BankingAccountCardManagementService bankingAccountCardManagementService;

    @Test
    @DisplayName("Should create a BankingCard for the BankingAccount")
    void shouldCreateCard() {
        // given
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setAccount(userAccount);

        setUpContext(customer);

        BankingAccount givenBankAccount = new BankingAccount(customer);
        givenBankAccount.setId(1L);
        givenBankAccount.setAccountNumber("US9900001111112233334444");

        BankingCard givenBankingCard = new BankingCard(givenBankAccount);
        givenBankingCard.setId(11L);
        givenBankingCard.setCardNumber("1234567890123456");

        BankingAccountCardRequest request = new BankingAccountCardRequest(BankingCardType.CREDIT);

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.of(givenBankAccount));
        when(bankingCardService.createBankingCard(any(BankingAccount.class), any(BankingCardType.class)))
                .thenReturn(givenBankingCard);

        BankingCard requestedBankingCard = bankingAccountCardManagementService.requestCard(
                givenBankAccount.getId(),
                request
        );

        // then
        assertThat(requestedBankingCard).isNotNull();
        assertThat(requestedBankingCard.getCardNumber()).isEqualTo(givenBankingCard.getCardNumber());
        assertThat(requestedBankingCard.getCardType()).isEqualTo(givenBankingCard.getCardType());
    }

    @Test
    @DisplayName("Should not create a BankingCard when banking account not found")
    void shouldFailToCreateCardWhenAccountNotFound() {
        // given
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setAccount(userAccount);

        setUpContext(customer);

        BankingAccount givenBankAccount = new BankingAccount(customer);
        givenBankAccount.setId(1L);
        givenBankAccount.setAccountNumber("US9900001111112233334444");

        BankingAccountCardRequest request = new BankingAccountCardRequest(BankingCardType.CREDIT);

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.empty());

        BankingAccountNotFoundException exception = assertThrows(
                BankingAccountNotFoundException.class,
                () -> bankingAccountCardManagementService.requestCard(
                        givenBankAccount.getId(),
                        request
                )
        );

        // then
        assertEquals(Exceptions.BANKING.ACCOUNT.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should fail to create a BankingCard when BankingAccount is not yours")
    void shouldFailToCreateCardWhenAccountIsNotYours() {
        // given
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setAccount(userAccount);

        UserAccount userAccountB = UserAccount.create()
                                              .setId(2L)
                                              .setEmail("customer@demo.com")
                                              .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customerB = Customer.create()
                                     .setId(2L)
                                     .setAccount(userAccountB);

        setUpContext(customer);

        BankingAccount givenBankAccount = new BankingAccount(customerB);
        givenBankAccount.setId(1L);
        givenBankAccount.setAccountNumber("US9900001111112233334444");

        BankingAccountCardRequest request = new BankingAccountCardRequest(BankingCardType.CREDIT);

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.of(givenBankAccount));

        BankingAccountOwnershipException exception = assertThrows(
                BankingAccountOwnershipException.class,
                () -> bankingAccountCardManagementService.requestCard(
                        givenBankAccount.getId(),
                        request
                )
        );

        // then
        assertEquals(Exceptions.BANKING.ACCOUNT.ACCESS_FORBIDDEN, exception.getMessage());
    }

    @Test
    @DisplayName("Should create a BankingCard when account is not yours but you are admin")
    void shouldCreateCardWhenAccountIsNotYoursButYouAreAdmin() {
        // given
        UserAccount adminAccount = UserAccount.create()
                                              .setId(1L)
                                              .setRole(UserAccountRole.ADMIN)
                                              .setEmail("customer@demo.com")
                                              .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer admin = Customer.create()
                                 .setId(1L)
                                 .setAccount(adminAccount);

        UserAccount userAccount = UserAccount.create()
                                             .setId(2L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(2L)
                                    .setAccount(userAccount);


        setUpContext(admin);

        BankingAccount givenBankAccount = new BankingAccount(customer);
        givenBankAccount.setId(1L);
        givenBankAccount.setAccountNumber("US9900001111112233334444");

        BankingCard givenBankingCard = new BankingCard(givenBankAccount);
        givenBankingCard.setId(11L);
        givenBankingCard.setCardNumber("1234567890123456");

        BankingAccountCardRequest request = new BankingAccountCardRequest(BankingCardType.CREDIT);

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.of(givenBankAccount));
        when(bankingCardService.createBankingCard(any(BankingAccount.class), any(BankingCardType.class)))
                .thenReturn(givenBankingCard);

        BankingCard requestedBankingCard = bankingAccountCardManagementService.requestCard(
                givenBankAccount.getId(),
                request
        );

        // then
        assertThat(requestedBankingCard).isNotNull();
        assertThat(requestedBankingCard.getCardNumber()).isEqualTo(givenBankingCard.getCardNumber());
        assertThat(requestedBankingCard.getCardType()).isEqualTo(givenBankingCard.getCardType());
    }

    @Test
    @DisplayName("Should fail to create a BankingCard when account reached limit")
    void shouldFailToCreateCardWhenAccountLimitReached() {
        // given
        UserAccount userAccount = UserAccount.create()
                                             .setId(1L)
                                             .setEmail("customer@demo.com")
                                             .setPassword(passwordEncoder.encode(RAW_PASSWORD));

        Customer customer = Customer.create()
                                    .setId(1L)
                                    .setAccount(userAccount);

        setUpContext(customer);

        BankingAccount givenBankAccount = new BankingAccount(customer);
        givenBankAccount.setId(1L);
        givenBankAccount.setAccountNumber("US9900001111112233334444");
        givenBankAccount.addBankingCard(new BankingCard());
        givenBankAccount.addBankingCard(new BankingCard());
        givenBankAccount.addBankingCard(new BankingCard());
        givenBankAccount.addBankingCard(new BankingCard());
        givenBankAccount.addBankingCard(new BankingCard());

        BankingAccountCardRequest request = new BankingAccountCardRequest(BankingCardType.CREDIT);

        // when
        when(bankingAccountRepository.findById(anyLong())).thenReturn(Optional.of(givenBankAccount));

        BankingAccountCardsLimitException exception = assertThrows(
                BankingAccountCardsLimitException.class,
                () -> bankingAccountCardManagementService.requestCard(
                        givenBankAccount.getId(),
                        request
                )
        );

        // then
        assertEquals(Exceptions.BANKING.ACCOUNT.CARD_LIMIT, exception.getMessage());
    }
}
