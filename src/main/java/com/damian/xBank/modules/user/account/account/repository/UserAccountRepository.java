package com.damian.xBank.modules.user.account.account.repository;

import com.damian.xBank.shared.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);

    boolean existsByEmail(String email);

}

