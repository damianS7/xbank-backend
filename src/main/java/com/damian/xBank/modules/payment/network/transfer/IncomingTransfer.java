package com.damian.xBank.modules.payment.network.transfer;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.payment.network.transfer.application.dto.request.IncomingTransferRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncomingTransfer {
    private final BankingAccountRepository bankingAccountRepository;

    public IncomingTransfer(
        BankingAccountRepository bankingAccountRepository
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
    }

    @Transactional
    public void execute(
        IncomingTransferRequest request
    ) {
        BankingAccount customerAccount = bankingAccountRepository
            .findByAccountNumber(request.toIban())
            .orElseThrow(
                () -> new BankingAccountNotFoundException(request.toIban())
            );

        BankingTransaction bankingTransaction = new BankingTransaction();
        bankingTransaction.setBankingAccount(customerAccount);
        bankingTransaction.setDescription("Incoming transfer");
        bankingTransaction.setAmount(request.amount());
        bankingTransaction.complete();

        customerAccount.addBalance(request.amount());
    }

}