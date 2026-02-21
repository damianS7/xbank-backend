package com.damian.xBank.modules.banking.transfer.application.usecase;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionType;
import com.damian.xBank.modules.banking.transaction.infrastructure.repository.BankingTransactionRepository;
import com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.request.IncomingTransferAuthorizedRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void execute(
        IncomingTransferAuthorizedRequest request
    ) {
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