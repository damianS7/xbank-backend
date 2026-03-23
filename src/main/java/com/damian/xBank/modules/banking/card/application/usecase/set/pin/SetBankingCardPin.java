package com.damian.xBank.modules.banking.card.application.usecase.set.pin;

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
 * Caso de uso para cambiar el PIN de la tarjeta.
 */
@Service
public class SetBankingCardPin {
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;
    private final BankingCardRepository bankingCardRepository;

    public SetBankingCardPin(
        AuthenticationContext authenticationContext,
        PasswordValidator passwordValidator,
        BankingCardRepository bankingCardRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
        this.bankingCardRepository = bankingCardRepository;
    }

    /**
     * @param command Comando con lo necesario para cambiar el pin
     */
    @Transactional
    public BankingCardResult execute(SetBankingCardPinCommand command) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        // Tarjeta a la que se cambia el PIN
        final BankingCard bankingCard = bankingCardRepository
            .findById(command.cardId())
            .orElseThrow(() -> new BankingCardNotFoundException(command.cardId()));

        // Comprobaciones de seguridad
        if (!currentUser.isAdmin()) {
            bankingCard.assertOwnedBy(currentUser.getId());
            passwordValidator.validatePassword(currentUser, command.password());
        }

        // Cambiar el PIN
        bankingCard.changePIN(command.pin());
        bankingCardRepository.save(bankingCard);

        return BankingCardResult.from(bankingCard);
    }
}