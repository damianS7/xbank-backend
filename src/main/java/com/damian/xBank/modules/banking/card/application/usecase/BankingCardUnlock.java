package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.card.application.dto.request.BankingCardUnlockRequest;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.user.account.account.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankingCardUnlock {
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;
    private final BankingCardRepository bankingCardRepository;

    public BankingCardUnlock(
            AuthenticationContext authenticationContext,
            PasswordValidator passwordValidator,
            BankingCardRepository bankingCardRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
        this.bankingCardRepository = bankingCardRepository;
    }

    /**
     * Unlocks the customer card by setting ACTIVE status
     *
     * @param cardId  the banking card id
     * @param request the request with the data needed to perfom the operation
     * @return BankingCard locked card
     */
    @Transactional
    public BankingCard execute(
            Long cardId,
            BankingCardUnlockRequest request
    ) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // Banking card to be locked
        final BankingCard bankingCard = bankingCardRepository.findById(cardId).orElseThrow(
                // Banking card not found
                () -> new BankingCardNotFoundException(cardId));

        // run validations if not admin
        if (!currentUser.isAdmin()) {

            bankingCard.assertOwnedBy(currentUser.getId());

            passwordValidator.validatePassword(currentUser, request.password());
        }

        // we mark the card as active
        bankingCard.setStatus(BankingCardStatus.ACTIVE);

        // save the data and return BankingAccount
        return bankingCardRepository.save(bankingCard);
    }
}