package com.damian.xBank.modules.banking.transfer.infrastructure.repository;

import com.damian.xBank.modules.banking.transfer.domain.entity.BankingTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankingTransferRepository extends JpaRepository<BankingTransfer, Long> {

}


