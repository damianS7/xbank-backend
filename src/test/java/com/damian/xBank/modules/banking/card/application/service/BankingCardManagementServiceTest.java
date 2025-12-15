package com.damian.xBank.modules.banking.card.application.service;

import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.enums.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdateDailyLimitRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdateLockRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdatePinRequest;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardOwnershipException;
import com.damian.xBank.modules.banking.card.infra.repository.BankingCardRepository;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.exception.UserAccountInvalidPasswordConfirmationException;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.AbstractServiceTest;
import com.damian.xBank.shared.exception.Exceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class BankingCardManagementServiceTest extends AbstractServiceTest {

    @Mock
    private BankingCardRepository bankingCardRepository;

    @InjectMocks
    private BankingCardManagementService bankingCardManagementService;

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
    @DisplayName("Should update card pin")
    void shouldUpdateCardPin() {
        // given
        setUpContext(customer);

        BankingCardUpdatePinRequest request = new BankingCardUpdatePinRequest("7777", RAW_PASSWORD);

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(customerBankingCard));
        when(bankingCardRepository.save(any(BankingCard.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        bankingCardManagementService.updatePin(customerBankingCard.getId(), request);

        // then
        assertThat(customerBankingCard).isNotNull();
        assertThat(customerBankingCard.getCardPin()).isEqualTo(request.pin());
        verify(bankingCardRepository, times(1)).save(any(BankingCard.class));
    }

    @Test
    @DisplayName("Should fail to update card pin when card not found")
    void shouldFailToUpdateCardPinWhenCardNotFound() {
        // given
        setUpContext(customer);

        BankingCardUpdatePinRequest request = new BankingCardUpdatePinRequest("7777", RAW_PASSWORD);

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.empty());

        BankingCardNotFoundException exception = assertThrows(
                BankingCardNotFoundException.class,
                () -> bankingCardManagementService.updatePin(1L, request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(Exceptions.BANKING.CARD.NOT_FOUND);
    }

    @Test
    @DisplayName("Should fail to update card pin when card not owner")
    void shouldFailToUpdateCardPinWhenNotOwner() {
        // given
        Customer customerNotOwner = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("customerNotOwner@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        setUpContext(customerNotOwner);

        BankingCardUpdatePinRequest request = new BankingCardUpdatePinRequest("7777", RAW_PASSWORD);

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(customerBankingCard));

        BankingCardOwnershipException exception = assertThrows(
                BankingCardOwnershipException.class,
                () -> bankingCardManagementService.updatePin(customerBankingCard.getId(), request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(Exceptions.BANKING.CARD.OWNERSHIP);
    }

    @Test
    @DisplayName("Should fail to update card pin when password is invalid")
    void shouldFailToUpdateCardPinWhenPasswordIsInvalid() {
        // given
        setUpContext(customer);

        BankingCardUpdatePinRequest request = new BankingCardUpdatePinRequest("7777", "BAD_PASSWORD");

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(customerBankingCard));

        UserAccountInvalidPasswordConfirmationException exception = assertThrows(
                UserAccountInvalidPasswordConfirmationException.class,
                () -> bankingCardManagementService.updatePin(customerBankingCard.getId(), request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(Exceptions.USER.ACCOUNT.INVALID_PASSWORD);
    }

    @Test
    @DisplayName("Should update card daily limit")
    void shouldUpdateCardDailyLimit() {
        // given
        setUpContext(customer);

        BankingCardUpdateDailyLimitRequest request = new BankingCardUpdateDailyLimitRequest(
                BigDecimal.valueOf(7777),
                RAW_PASSWORD
        );

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(customerBankingCard));
        when(bankingCardRepository.save(any(BankingCard.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        bankingCardManagementService.updateDailyLimit(customerBankingCard.getId(), request);

        // then
        assertThat(customerBankingCard).isNotNull();
        assertThat(customerBankingCard.getDailyLimit()).isEqualTo(request.dailyLimit());
        verify(bankingCardRepository, times(1)).save(any(BankingCard.class));
    }

    @Test
    @DisplayName("Should fail to update card daily limit when card not found")
    void shouldFailToUpdateCardDailyLimitWhenCardNotFound() {
        // given
        setUpContext(customer);

        BankingCardUpdateDailyLimitRequest request = new BankingCardUpdateDailyLimitRequest(
                BigDecimal.valueOf(7777),
                RAW_PASSWORD
        );

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.empty());

        BankingCardNotFoundException exception = assertThrows(
                BankingCardNotFoundException.class,
                () -> bankingCardManagementService.updateDailyLimit(1L, request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(Exceptions.BANKING.CARD.NOT_FOUND);
    }

    @Test
    @DisplayName("Should fail to update card daily limit when not owner")
    void shouldFailToUpdateCardDailyLimitWhenNotOwner() {
        // given
        Customer customerNotOwner = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("customerNotOwner@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        setUpContext(customerNotOwner);

        BankingCardUpdateDailyLimitRequest request = new BankingCardUpdateDailyLimitRequest(
                BigDecimal.valueOf(7777),
                RAW_PASSWORD
        );

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(customerBankingCard));

        BankingCardOwnershipException exception = assertThrows(
                BankingCardOwnershipException.class,
                () -> bankingCardManagementService.updateDailyLimit(customerBankingCard.getId(), request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(Exceptions.BANKING.CARD.OWNERSHIP);
    }

    @Test
    @DisplayName("Should fail to update card daily limit when password is invalid")
    void shouldFailToUpdateCardDailyLimitWhenPasswordIsInvalid() {
        // given
        setUpContext(customer);

        BankingCardUpdateDailyLimitRequest request = new BankingCardUpdateDailyLimitRequest(
                BigDecimal.valueOf(7777),
                "BAD_PASSWORD"
        );

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(customerBankingCard));

        UserAccountInvalidPasswordConfirmationException exception = assertThrows(
                UserAccountInvalidPasswordConfirmationException.class,
                () -> bankingCardManagementService.updateDailyLimit(customerBankingCard.getId(), request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(Exceptions.USER.ACCOUNT.INVALID_PASSWORD);
    }

    @Test
    @DisplayName("Should lock card")
    void shouldLockCard() {
        // given
        customerBankingCard.setCardStatus(BankingCardStatus.ACTIVE);
        setUpContext(customer);

        BankingCardUpdateLockRequest givenRequest = new BankingCardUpdateLockRequest(
                RAW_PASSWORD
        );

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(customerBankingCard));
        when(bankingCardRepository.save(any(BankingCard.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        bankingCardManagementService.updateLockStatus(
                customerBankingCard.getId(),
                givenRequest
        );

        // then
        assertThat(customerBankingCard).isNotNull();
        assertThat(customerBankingCard.getStatus()).isEqualTo(BankingCardStatus.LOCKED);
        verify(bankingCardRepository, times(1)).save(any(BankingCard.class));
    }

    @Test
    @DisplayName("Should fail to update card lock status when card not found")
    void shouldFailToUpdateCardLockStatusWhenCardNotFound() {
        // given
        setUpContext(customer);

        BankingCardUpdatePinRequest request = new BankingCardUpdatePinRequest("7777", RAW_PASSWORD);

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.empty());

        BankingCardNotFoundException exception = assertThrows(
                BankingCardNotFoundException.class,
                () -> bankingCardManagementService.updatePin(1L, request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(Exceptions.BANKING.CARD.NOT_FOUND);
    }

    @Test
    @DisplayName("Should fail to update card lock status when not owner")
    void shouldFailToUpdateUpdateCardLockStatusWhenNotOwner() {
        // given
        Customer customerNotOwner = Customer.create(
                UserAccount.create()
                           .setId(2L)
                           .setEmail("customerNotOwner@demo.com")
                           .setPassword(bCryptPasswordEncoder.encode(RAW_PASSWORD))
        ).setId(2L);

        setUpContext(customerNotOwner);

        BankingCardUpdatePinRequest request = new BankingCardUpdatePinRequest("7777", RAW_PASSWORD);

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(customerBankingCard));

        BankingCardOwnershipException exception = assertThrows(
                BankingCardOwnershipException.class,
                () -> bankingCardManagementService.updatePin(customerBankingCard.getId(), request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(Exceptions.BANKING.CARD.OWNERSHIP);
    }

    @Test
    @DisplayName("Should fail to update card lock status when password is invalid")
    void shouldFailToUpdateUpdateCardLockStatusWhenPasswordIsInvalid() {
        // given
        setUpContext(customer);

        BankingCardUpdatePinRequest request = new BankingCardUpdatePinRequest("7777", "BAD_PASSWORD");

        // when
        when(bankingCardRepository.findById(anyLong())).thenReturn(Optional.of(customerBankingCard));

        UserAccountInvalidPasswordConfirmationException exception = assertThrows(
                UserAccountInvalidPasswordConfirmationException.class,
                () -> bankingCardManagementService.updatePin(customerBankingCard.getId(), request)
        );

        // then
        assertThat(exception.getMessage()).isEqualTo(Exceptions.USER.ACCOUNT.INVALID_PASSWORD);
    }
}
