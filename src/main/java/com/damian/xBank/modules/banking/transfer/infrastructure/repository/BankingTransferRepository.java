package com.damian.xBank.modules.banking.transfer.infrastructure.repository;

import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankingTransferRepository extends JpaRepository<BankingTransfer, Long> {
    Page<BankingTransfer> findAllByStatus(BankingTransferStatus status, Pageable pageable);

    Page<BankingTransfer> findAllByFromAccount_UserId(Long userId, Pageable pageable);

    Optional<BankingTransfer> findByProviderAuthorizationId(String providerAuthorizationId);
}


