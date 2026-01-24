package com.damian.xBank.modules.payment.intent.infrastructure.repository;

import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentIntentRepository extends JpaRepository<PaymentIntent, Long> {
}

