package com.damian.whatsapp.modules.user.account.token;

import com.damian.whatsapp.shared.domain.UserAccountToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountTokenRepository extends JpaRepository<UserAccountToken, Long> {
    Optional<UserAccountToken> findByAccount_Id(Long accountId);

    Optional<UserAccountToken> findByToken(String token);
}

