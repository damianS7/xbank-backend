package com.damian.xBank.modules.banking.transfer.incoming.infrastructure.repository;

import com.damian.xBank.modules.banking.transfer.incoming.domain.model.IncomingTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IncomingTransferRepository extends JpaRepository<IncomingTransfer, Long> {
    Optional<IncomingTransfer> findByProviderAuthorizationId(String providerAuthorizationId);
}


