package com.damian.xBank.modules.user.token.infrastructure.repository;

import com.damian.xBank.modules.user.token.domain.model.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    Optional<UserToken> findByUser_Id(Long accountId);

    Optional<UserToken> findByToken(String token);
}

