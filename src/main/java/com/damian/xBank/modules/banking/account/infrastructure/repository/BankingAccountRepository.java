package com.damian.xBank.modules.banking.account.infrastructure.repository;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface BankingAccountRepository extends JpaRepository<BankingAccount, Long> {
    Set<BankingAccount> findByCustomer_Id(Long customerId);

    Optional<BankingAccount> findByAccountNumber(String accountNumber);
}

