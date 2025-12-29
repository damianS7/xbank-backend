package com.damian.xBank.modules.banking.card.application.service;

import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardSpendRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardWithdrawRequest;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.application.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transaction.domain.service.BankingTransactionDomainService;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.notification.domain.model.NotificationEvent;
import com.damian.xBank.modules.notification.domain.model.NotificationType;
import com.damian.xBank.modules.notification.infrastructure.service.NotificationPublisher;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Service
public class BankingCardOperationService {

    private final BankingTransactionDomainService bankingTransactionDomainService;
    private final BankingCardRepository bankingCardRepository;
    private final NotificationPublisher notificationPublisher;
    private final AuthenticationContext authenticationContext;

    public BankingCardOperationService(
            BankingTransactionDomainService bankingTransactionDomainService,
            BankingCardRepository bankingCardRepository,
            NotificationPublisher notificationPublisher,
            AuthenticationContext authenticationContext
    ) {
        this.bankingTransactionDomainService = bankingTransactionDomainService;
        this.bankingCardRepository = bankingCardRepository;
        this.notificationPublisher = notificationPublisher;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Spend money from a card
     *
     * @param bankingCardId the id of the card to spend from
     * @param request       the request with the data needed to perfom the operation
     * @return the created transaction
     */
    public BankingTransaction spend(Long bankingCardId, BankingCardSpendRequest request) {
        BankingCard bankingCard = bankingCardRepository.findById(bankingCardId).orElseThrow(
                () -> new BankingCardNotFoundException(bankingCardId)
        );

        return this.executeOperation(
                bankingCard,
                request.cardPIN(),
                request.amount(),
                request.description(),
                BankingTransactionType.CARD_CHARGE
        );
    }

    /**
     * Withdraw money from ATM machine
     *
     * @param bankingCardId the id of the card to withdraw from
     * @param request       the request with the data needed to perfom the operation
     * @return the created transaction
     */
    public BankingTransaction withdraw(Long bankingCardId, BankingCardWithdrawRequest request) {
        BankingCard bankingCard = bankingCardRepository.findById(bankingCardId).orElseThrow(
                () -> new BankingCardNotFoundException(bankingCardId)
        );

        return this.executeOperation(
                bankingCard,
                request.cardPIN(),
                request.amount(),
                "ATM Withdrawal.",
                BankingTransactionType.WITHDRAWAL
        );
    }

    /**
     * Handle the core logic of charging an amount to a card.
     * It performs all necessary validations and creates the transaction.
     * <p>
     * It's private to ensure that all card operations go through the public methods.
     *
     * @param card
     * @param cardPin
     * @param amount
     * @param description
     * @param transactionType
     * @return the created transaction
     */
    private BankingTransaction executeOperation(
            BankingCard card,
            String cardPin,
            BigDecimal amount,
            String description,
            BankingTransactionType transactionType
    ) {
        final Customer customerLogged = authenticationContext.getCurrentCustomer();

        // run validations for the card and throw exception
        card.assertOwnedBy(customerLogged.getId())
            .assertUsable()
            .assertCorrectPin(cardPin)
            .assertSufficientFunds(amount);

        BankingTransaction transaction = BankingTransaction
                .create(
                        transactionType,
                        card.getBankingAccount(),
                        amount
                )
                .setBankingCard(card)
                .setDescription(description);

        // store here the transaction as PENDING
        bankingTransactionDomainService.record(transaction);

        // Notify the user
        notificationPublisher.publish(
                new NotificationEvent(
                        customerLogged.getAccount().getId(),
                        NotificationType.TRANSACTION,
                        Map.of(
                                "transaction", BankingTransactionDtoMapper.toBankingTransactionDto(transaction)
                        ),
                        Instant.now().toString()
                )
        );

        return transaction;
    }
}
