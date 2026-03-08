package com.damian.xBank.modules.banking.transfer.application.usecase.transfer.incoming;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.banking.transfer.infrastructure.rest.dto.request.IncomingTransferRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * TODO Merge with AuthorizeIncomingTrnasfer???
 * Use case for processing an incoming transfer. This involves:
 * 1. Validating the incoming transfer request.
 * 2. Updating the recipient's account balance.
 * 3. Recording the transaction in the banking transaction history.
 */
@Service
public class ProcessIncomingTransfer {
    private final BankingAccountRepository bankingAccountRepository;
    private final BankingTransactionRepository bankingTransactionRepository;

    public ProcessIncomingTransfer(
        BankingAccountRepository bankingAccountRepository,
        BankingTransactionRepository bankingTransactionRepository
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.bankingTransactionRepository = bankingTransactionRepository;
    }

    @Transactional
    public void execute(IncomingTransferRequest request) {
        BankingAccount customerAccount = bankingAccountRepository
            .findByAccountNumber(request.toIban())
            .orElseThrow(
                () -> new BankingAccountNotFoundException(request.toIban())
            );

        BankingTransaction bankingTransaction = new BankingTransaction();
        bankingTransaction.setType(BankingTransactionType.TRANSFER_FROM);
        bankingTransaction.setBankingAccount(customerAccount);
        bankingTransaction.setDescription("Incoming transfer. reference: " + request.reference());
        bankingTransaction.setAmount(request.amount());
        bankingTransaction.complete();

        customerAccount.addBalance(request.amount());

        bankingTransactionRepository.save(bankingTransaction);
    }
}