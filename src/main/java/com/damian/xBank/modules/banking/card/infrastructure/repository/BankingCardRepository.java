package com.damian.xBank.modules.banking.card.infrastructure.repository;

import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.model.CardNumber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface BankingCardRepository extends JpaRepository<BankingCard, Long> {
    Optional<BankingCard> findByCardNumber(CardNumber cardNumber);

    boolean existsByCardNumber(String cardNumber);

    Set<BankingCard> findByBankingAccountId(Long bankingAccountId);

    @Query("SELECT cards FROM BankingCard cards WHERE cards.bankingAccount.user.id = :userId")
    Set<BankingCard> findCardsByUserId(@Param("userId") Long userId);

    @Query(
        """
                SELECT c
                FROM BankingCard c
                WHERE c.status <> :status
                AND (
                    c.expiration.year < :year
                    OR (c.expiration.year = :year AND c.expiration.month <= :month)
                )
            """
    )
    Page<BankingCard> findExpiredCards(
        @Param("status") BankingCardStatus status,
        @Param("year") int year,
        @Param("month") int month,
        Pageable pageable
    );
}


