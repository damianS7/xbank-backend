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

/**
 * Caso de uso donde se establece el lìmite diario de gasto de una tarjeta
 */
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
     * @param command Comando con los datos necesarios
     * @return Result con el nuevo límite establecido
     */
    @Transactional
    public BankingCardResult execute(SetBankingCardDailyLimitCommand command) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        // La tarjeta a la que se cambia el límite
        final BankingCard bankingCard = bankingCardRepository
            .findById(command.cardId())
            .orElseThrow(() -> new BankingCardNotFoundException(command.cardId()));

        // Comprobar seguridad
        if (!currentUser.isAdmin()) {
            bankingCard.assertOwnedBy(currentUser.getId());
            passwordValidator.validatePassword(currentUser, command.password());
        }

        // Establecer el nuevo límite
        bankingCard.limit(command.dailyLimit());
        bankingCardRepository.save(bankingCard);

        return BankingCardResult.from(bankingCard);
    }

}