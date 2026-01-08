package com.damian.xBank.modules.banking.transfer.infrastructure.repository;

import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface BankingTransferRepository extends JpaRepository<BankingTransfer, Long> {

    Set<BankingTransfer> findAllByFromAccount_UserId(Long userId);
}


