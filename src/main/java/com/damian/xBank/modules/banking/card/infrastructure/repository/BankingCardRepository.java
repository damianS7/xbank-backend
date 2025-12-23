package com.damian.xBank.modules.banking.card.infrastructure.repository;

import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface BankingCardRepository extends JpaRepository<BankingCard, Long> {
    Set<BankingCard> findByBankingAccountId(Long bankingAccountId);

    @Query("SELECT cards FROM BankingCard cards WHERE cards.bankingAccount.customer.id = :customerId")
    Set<BankingCard> findCardsByCustomerId(@Param("customerId") Long customerId);


    Set<BankingCard> findByCardStatusNotAndExpiredDateLessThanEqual(
            BankingCardStatus status,
            LocalDate expiredDate
    );
}


