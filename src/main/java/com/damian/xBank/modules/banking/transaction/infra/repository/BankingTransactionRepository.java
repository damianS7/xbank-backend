package com.damian.xBank.modules.banking.transaction.infra.repository;

import com.damian.xBank.modules.banking.transaction.domain.entity.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.enums.BankingTransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankingTransactionRepository extends JpaRepository<BankingTransaction, Long> {
    Page<BankingTransaction> findByBankingCardId(Long bankingCardId, Pageable pageable);

    Page<BankingTransaction> findByBankingAccountId(Long bankingAccountId, Pageable pageable);

    Page<BankingTransaction> findByStatusAndBankingAccount_Customer_Id(
            BankingTransactionStatus status,
            Long customerId,
            Pageable pageable
    );
}

