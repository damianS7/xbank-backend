package com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.repository;

import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransfer;
import com.damian.xBank.modules.banking.transfer.outgoing.domain.model.OutgoingTransferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OutgoingTransferRepository extends JpaRepository<OutgoingTransfer, Long> {
    Page<OutgoingTransfer> findAllByStatus(OutgoingTransferStatus status, Pageable pageable);

    Page<OutgoingTransfer> findAllByFromAccount_UserId(Long userId, Pageable pageable);

    Optional<OutgoingTransfer> findByProviderAuthorizationId(String providerAuthorizationId);
}


