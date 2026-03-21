package com.damian.xBank.modules.banking.card.application.usecase.get;

import com.damian.xBank.modules.banking.card.application.dto.BankingCardResult;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Caso de uso donde el usuario obtiene todas sus tarjetas.
 */
@Service
public class GetAllCurrentUserBankingCards {
    private final AuthenticationContext authenticationContext;
    private final BankingCardRepository bankingCardRepository;

    public GetAllCurrentUserBankingCards(
        AuthenticationContext authenticationContext,
        BankingCardRepository bankingCardRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.bankingCardRepository = bankingCardRepository;
    }

    /**
     * @param query Consulta con los datos necesarios
     * @return Result con todas las tarjetas
     */
    public GetAllCurrentUserBankingCardsResult execute(GetAllCurrentUserCardsQuery query) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        Set<BankingCard> bankingCards = bankingCardRepository.findCardsByUserId(currentUser.getId());

        return new GetAllCurrentUserBankingCardsResult(
            bankingCards.stream()
                .map(BankingCardResult::from)
                .collect(Collectors.toSet())
        );
    }
}