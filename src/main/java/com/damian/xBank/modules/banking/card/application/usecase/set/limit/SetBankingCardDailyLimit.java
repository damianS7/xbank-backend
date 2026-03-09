package com.damian.xBank.modules.banking.card.application.usecase.set.limit;

import com.damian.xBank.modules.banking.card.application.dto.BankingCardResult;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class SetBankingCardDailyLimit {
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;
    private final BankingCardRepository bankingCardRepository;

    public SetBankingCardDailyLimit(
        AuthenticationContext authenticationContext,
        PasswordValidator passwordValidator,
        BankingCardRepository bankingCardRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
        this.bankingCardRepository = bankingCardRepository;
    }


    /**
     * Update the daily limit of the card.
     *
     * @param command the command with the data needed to perfom the operation
     * @return BankingCard the updated card
     */
    @Transactional
    public BankingCardResult execute(SetBankingCardDailyLimitCommand command) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        // Banking card to set limit on
        final BankingCard bankingCard = bankingCardRepository.findById(command.cardId()).orElseThrow(
            // Banking card not found
            () -> new BankingCardNotFoundException(command.cardId()));

        // run validations if not admin
        if (!currentUser.isAdmin()) {

            bankingCard.assertOwnedBy(currentUser.getId());

            passwordValidator.validatePassword(currentUser, command.password());
        }

        // we set the limit of the card
        bankingCard.setDailyLimit(command.dailyLimit());

        // we change the updateAt timestamp field
        bankingCard.setUpdatedAt(Instant.now());

        // save the data and return BankingCard
        bankingCardRepository.save(bankingCard);

        return BankingCardResult.from(bankingCard);
    }

}