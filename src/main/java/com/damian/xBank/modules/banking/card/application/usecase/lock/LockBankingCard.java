package com.damian.xBank.modules.banking.card.application.usecase.lock;

import com.damian.xBank.modules.banking.card.application.dto.BankingCardResult;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso donde el usuario bloquea su tarjeta.
 */
@Service
public class LockBankingCard {
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;
    private final BankingCardRepository bankingCardRepository;

    public LockBankingCard(
        AuthenticationContext authenticationContext,
        PasswordValidator passwordValidator,
        BankingCardRepository bankingCardRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
        this.bankingCardRepository = bankingCardRepository;
    }


    /**
     * @param command Comando con lo necesario para bloquear la tarjeta
     * @return La tarjeta bloqueada
     */
    @Transactional
    public BankingCardResult execute(LockBankingCardCommand command) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        // La tarjeta a bloquear
        final BankingCard bankingCard = bankingCardRepository
            .findById(command.cardId())
            .orElseThrow(() -> new BankingCardNotFoundException(command.cardId()));

        // Si el usuario no es admin comprobar que el dueño de la tarjeta.
        if (!currentUser.isAdmin()) {
            bankingCard.assertOwnedBy(currentUser.getId());
            passwordValidator.validatePassword(currentUser, command.password());
        }

        // Bloquea la tarjeta
        bankingCard.lock();
        bankingCardRepository.save(bankingCard);

        return BankingCardResult.from(bankingCard);
    }
}