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
 * Caso de uso donde el usuario desbloquea una tarjeta.
 */
@Service
public class UnlockBankingCard {
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;
    private final BankingCardRepository bankingCardRepository;

    public UnlockBankingCard(
        AuthenticationContext authenticationContext,
        PasswordValidator passwordValidator,
        BankingCardRepository bankingCardRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
        this.bankingCardRepository = bankingCardRepository;
    }

    /**
     * @param command Comando con los datos necesarios para el desbloqueo
     */
    @Transactional
    public BankingCardResult execute(UnlockBankingCardCommand command) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        // Tarjeta a desbloquear
        final BankingCard bankingCard = bankingCardRepository
            .findById(command.cardId())
            .orElseThrow(() -> new BankingCardNotFoundException(command.cardId()));

        // Comprobaciones de seguridad
        if (!currentUser.isAdmin()) {
            bankingCard.assertOwnedBy(currentUser.getId());
            passwordValidator.validatePassword(currentUser, command.password());
        }

        // Desbloquear la tarjeta
        bankingCard.unlock();
        bankingCardRepository.save(bankingCard);

        return BankingCardResult.from(bankingCard);
    }
}