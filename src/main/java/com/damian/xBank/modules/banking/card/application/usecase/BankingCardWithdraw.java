package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardWithdrawRequest;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.application.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.service.BankingTransactionPersistenceService;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.domain.model.NotificationType;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
public class BankingCardWithdraw {
    private final AuthenticationContext authenticationContext;
    private final BankingCardRepository bankingCardRepository;
    private final NotificationPublisher notificationPublisher;
    private final BankingTransactionPersistenceService bankingTransactionPersistenceService;

    public BankingCardWithdraw(
            AuthenticationContext authenticationContext,
            BankingCardRepository bankingCardRepository,
            NotificationPublisher notificationPublisher,
            BankingTransactionPersistenceService bankingTransactionPersistenceService
    ) {
        this.authenticationContext = authenticationContext;
        this.bankingCardRepository = bankingCardRepository;
        this.notificationPublisher = notificationPublisher;
        this.bankingTransactionPersistenceService = bankingTransactionPersistenceService;
    }

    /**
     * Withdraw money from ATM machine
     *
     * @param bankingCardId the id of the card to withdraw from
     * @param request       the request with the data needed to perfom the operation
     * @return the created transaction
     */
    @Transactional
    public BankingTransaction execute(Long bankingCardId, BankingCardWithdrawRequest request) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        BankingCard bankingCard = bankingCardRepository.findById(bankingCardId).orElseThrow(
                () -> new BankingCardNotFoundException(bankingCardId)
        );

        // run validations for the card and throw exception
        bankingCard.assertCanSpend(currentUser, request.amount(), request.cardPIN());

        BankingTransaction transaction = BankingTransaction
                .create(
                        BankingTransactionType.WITHDRAWAL,
                        bankingCard.getBankingAccount(),
                        request.amount()
                )
                .setBankingCard(bankingCard)
                .setStatus(BankingTransactionStatus.COMPLETED)
                .setDescription("ATM Withdrawal.");

        bankingCard.chargeAmount(request.amount());

        // store here the transaction as PENDING
        bankingTransactionPersistenceService.record(transaction);

        // Notify the user
        notificationPublisher.publish(
                new NotificationEvent(
                        currentUser.getId(),
                        NotificationType.TRANSACTION,
                        Map.of(
                                "transaction", BankingTransactionDtoMapper.toBankingTransactionDto(transaction)
                        ),
                        Instant.now().toString()
                )
        );

        return transaction;

        // save the data and return BankingAccount
        //        return bankingCardRepository.save(bankingCard);
    }
}