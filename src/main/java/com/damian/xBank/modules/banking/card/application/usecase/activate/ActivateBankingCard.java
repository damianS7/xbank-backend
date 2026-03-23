package com.damian.xBank.modules.banking.card.application.usecase.activate;

import com.damian.xBank.modules.banking.card.application.dto.BankingCardResult;
import com.damian.xBank.modules.banking.card.domain.exception.BankingCardNotFoundException;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso donde el usuario activa una tarjeta en estado PENDING
 */
@Service
public class ActivateBankingCard {
    private final AuthenticationContext authenticationContext;
    private final BankingCardRepository bankingCardRepository;

    public ActivateBankingCard(
        AuthenticationContext authenticationContext,
        BankingCardRepository bankingCardRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.bankingCardRepository = bankingCardRepository;
    }

    /**
     * @param command ActivateBankingCardCommand
     * @return BankingCardResult
     */
    @Transactional
    public BankingCardResult execute(ActivateBankingCardCommand command) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        // Card que se activara
        final BankingCard bankingCard = bankingCardRepository
            .findById(command.cardId())
            .orElseThrow(() -> new BankingCardNotFoundException(command.cardId()));

        // Si no se es admin comprueba que la tarjeta le pertenece al usuario
        if (!currentUser.isAdmin()) {
            bankingCard.assertOwnedBy(currentUser.getId());
        }

        // Activa la tarjeta
        bankingCard.activate(command.cvv());
        bankingCardRepository.save(bankingCard);

        return BankingCardResult.from(bankingCard);
    }
}