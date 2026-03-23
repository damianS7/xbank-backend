package com.damian.xBank.modules.banking.transaction.infrastructure.repository;

import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankingTransactionRepository extends JpaRepository<BankingTransaction, Long> {
    Optional<BankingTransaction> findByAuthorizationId(String authorizationId);

    Page<BankingTransaction> findByBankingCard_Id(Long bankingCardId, Pageable pageable);

    Page<BankingTransaction> findByBankingAccount_Id(Long bankingAccountId, Pageable pageable);

    Page<BankingTransaction> findByStatusAndBankingAccount_User_Id(
        BankingTransactionStatus status,
        Long userId,
        Pageable pageable
    );
}

