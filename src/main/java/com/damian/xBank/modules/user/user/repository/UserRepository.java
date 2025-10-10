package com.damian.whatsapp.modules.user.user.repository;

import com.damian.whatsapp.shared.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserAccount_Email(String email);

    Optional<User> findByUserNameIgnoreCase(String username);

    boolean existsByUserAccount_Email(String email);
}

