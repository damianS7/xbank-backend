package com.damian.xBank.modules.user.customer.infra.repository;

import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByAccount_Email(String email);
}

