package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUpdatePinRequest;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class BankingCardSetPin {
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;
    private final BankingCardRepository bankingCardRepository;

    public BankingCardSetPin(
            AuthenticationContext authenticationContext,
            PasswordValidator passwordValidator,
            BankingCardRepository bankingCardRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
        this.bankingCardRepository = bankingCardRepository;
    }

    /**
     * Updates card pin
     *
     * @param bankingCardId the banking card id
     * @param request       the request with the data needed to perfom the operation
     * @return BankingCard the updated card
     */
    @Transactional
    public BankingCard execute(Long bankingCardId, BankingCardUpdatePinRequest request) {
        // Customer logged
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        // Banking card to set pin on
        final BankingCard bankingCard = bankingCardRepository.findById(bankingCardId).orElseThrow(
                // Banking card not found
                () -> new BankingCardNotFoundException(bankingCardId));

        // run validations if not admin
        if (!currentCustomer.isAdmin()) {

            bankingCard.assertOwnedBy(currentCustomer.getId());

            passwordValidator.validatePassword(currentCustomer.getAccount(), request.password());
        }

        // we set the new pin
        bankingCard.setCardPin(request.pin());

        // we change the updateAt timestamp field
        bankingCard.setUpdatedAt(Instant.now());

        // save the data and return BankingAccount
        return bankingCardRepository.save(bankingCard);
    }
}