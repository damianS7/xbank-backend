package com.damian.xBank.modules.banking.payment.application.usecase;


import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.payment.application.dto.request.PaymentCreateRequest;
import com.damian.xBank.modules.banking.payment.domain.model.Payment;
import com.damian.xBank.modules.banking.payment.domain.model.PaymentStatus;
import com.damian.xBank.modules.banking.payment.infrastructure.repository.PaymentRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class PaymentCreate {
    private final PaymentRepository paymentRepository;
    private final AuthenticationContext authenticationContext;

    public PaymentCreate(
            PaymentRepository paymentRepository,
            AuthenticationContext authenticationContext
    ) {
        this.paymentRepository = paymentRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     *
     * @param
     * @return
     */
    @Transactional
    public Payment execute(PaymentCreateRequest request) {
        // Current user
        final User currentMerchant = authenticationContext.getCurrentUser();

        Payment payment = new Payment();
        payment.setCurrency(BankingAccountCurrency.valueOf(request.currency()));
        payment.setInvoiceId(request.invoiceId());
        payment.setAmount(request.amount());
        payment.setMerchant(currentMerchant.getProfile().getFullName());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(Instant.now());
        payment.setUpdatedAt(Instant.now());

        return paymentRepository.save(payment);
    }
}