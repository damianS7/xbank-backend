package com.damian.xBank.modules.banking.card.application.service;

import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardSpendRequest;
import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardWithdrawRequest;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.application.guard.BankingCardGuard;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.infra.repository.BankingCardRepository;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.application.service.BankingTransactionAccountService;
import com.damian.xBank.modules.banking.transaction.application.service.BankingTransactionCardService;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.utils.AuthHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BankingCardOperationService {

    private final BankingTransactionAccountService bankingTransactionAccountService;
    private final BankingTransactionCardService bankingTransactionCardService;
    private final BankingCardRepository bankingCardRepository;

    public BankingCardOperationService(
            BankingTransactionAccountService bankingTransactionAccountService,
            BankingTransactionCardService bankingTransactionCardService,
            BankingCardRepository bankingCardRepository
    ) {
        this.bankingTransactionAccountService = bankingTransactionAccountService;
        this.bankingTransactionCardService = bankingTransactionCardService;
        this.bankingCardRepository = bankingCardRepository;
    }

    public BankingTransaction spend(Long bankingCardId, BankingCardSpendRequest request) {
        BankingCard bankingCard = bankingCardRepository.findById(bankingCardId).orElseThrow(
                () -> new BankingCardNotFoundException(bankingCardId)
        );

        return this.spend(bankingCard, request.cardPIN(), request.amount(), request.description());
    }

    public BankingTransaction withdraw(Long bankingCardId, BankingCardWithdrawRequest request) {
        BankingCard bankingCard = bankingCardRepository.findById(bankingCardId).orElseThrow(
                () -> new BankingCardNotFoundException(bankingCardId)
        );

        return this.withdrawal(bankingCard, request.cardPIN(), request.amount());
    }

    // withdraws money
    public BankingTransaction withdrawal(
            BankingCard card,
            String cardPin,
            BigDecimal amount
    ) {
        // TODO refactor to use spend method. passing transaction type
        //        return this.spend(card, cardPin, amount, "ATM Withdrawal.");
        final Customer customerLogged = AuthHelper.getCurrentCustomer();

        // run validations and throw if any throw exception
        BankingCardGuard.forCard(card)
                        .ownership(customerLogged)
                        .active()
                        .PIN(cardPin)
                        .sufficientFunds(amount);

        BankingTransaction transaction = bankingTransactionCardService.createTransaction(
                card,
                BankingTransactionType.WITHDRAWAL,
                amount,
                "ATM withdrawal."
        );

        // if the transaction is created, deduce the amount from balance
        card.chargeAmount(amount);

        // transaction is completed
        transaction.setStatus(BankingTransactionStatus.COMPLETED);

        // save the transaction
        return bankingTransactionAccountService.persistTransaction(transaction);
    }

    // validates card status and does the transaction
    public BankingTransaction spend(
            BankingCard card,
            String cardPIN,
            BigDecimal amount,
            String description
    ) {
        final Customer customerLogged = AuthHelper.getCurrentCustomer();

        // run validations and throw if any throw exception
        BankingCardGuard
                .forCard(card)
                .canSpend(customerLogged, cardPIN, amount);

        // store here the transaction as PENDING
        BankingTransaction transaction = bankingTransactionCardService.generateTransaction(
                card,
                BankingTransactionType.CARD_CHARGE,
                amount,
                description
        );

        // if the transaction is created, deduce the amount from balance
        // amount is deducted only after transaction is stored on db
        card.chargeAmount(amount);

        // After amount is deducted, mark the transaction as completed
        transaction.setStatus(BankingTransactionStatus.COMPLETED);

        // save the transaction
        return bankingTransactionAccountService.persistTransaction(transaction);
    }
}
