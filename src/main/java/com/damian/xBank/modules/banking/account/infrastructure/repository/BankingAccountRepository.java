package com.damian.xBank.modules.banking.account.infrastructure.repository;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface BankingAccountRepository extends JpaRepository<BankingAccount, Long> {
    Set<BankingAccount> findByUser_Id(Long userId);

    Optional<BankingAccount> findByAccountNumber(String accountNumber);

    @Query(
            value = """
                        WITH days AS (
                            SELECT generate_series(
                                DATE_TRUNC('year', CURRENT_DATE),
                                CURRENT_DATE,
                                INTERVAL '1 day'
                            )::date AS day
                        ),
                        accounts AS (
                            SELECT id
                            FROM banking_accounts
                            WHERE user_id = :userId
                              AND account_currency::text = :currency
                        ),
                        daily_account_balance AS (
                            SELECT
                                d.day,
                                a.id AS account_id,
                                (
                                    SELECT t.balance_after
                                    FROM banking_transactions t
                                    WHERE t.account_id = a.id
                                      AND t.status = 'COMPLETED'
                                      AND t.created_at < d.day + INTERVAL '1 day'
                                    ORDER BY t.created_at DESC
                                    LIMIT 1
                                ) AS balance
                            FROM days d
                            CROSS JOIN accounts a
                        )
                        SELECT
                            day,
                            COALESCE(SUM(balance), 0) AS total_balance
                        FROM daily_account_balance
                        GROUP BY day
                        ORDER BY day;
                    """,
            nativeQuery = true
    )
    Set<Object> findDailyBalancesForUserAndCurrency(
            @Param("userId") Long userId,
            @Param("currency") String currency
    );
}

