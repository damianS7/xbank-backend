package com.damian.xBank.modules.banking.transfer.infrastructure.repository;

import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransfer;
import com.damian.xBank.modules.banking.transfer.domain.model.BankingTransferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface BankingTransferRepository extends JpaRepository<BankingTransfer, Long> {
    Set<BankingTransfer> findAllByStatus(BankingTransferStatus status);

    Page<BankingTransfer> findAllByFromAccount_UserId(Long userId, Pageable pageable);

    Optional<BankingTransfer> findByProviderAuthorizationId(String providerAuthorizationId);
}


