package com.damian.xBank.modules.user.account.token.repository;

import com.damian.xBank.modules.user.account.token.model.UserAccountToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountTokenRepository extends JpaRepository<UserAccountToken, Long> {
    Optional<UserAccountToken> findByAccount_Id(Long accountId);

    Optional<UserAccountToken> findByToken(String token);
}

