package com.damian.xBank.modules.banking.card.application.usecase;

import com.damian.xBank.modules.banking.card.application.cqrs.query.GetAllCurrentUserCardsQuery;
import com.damian.xBank.modules.banking.card.application.cqrs.result.BankingCardResult;
import com.damian.xBank.modules.banking.card.application.cqrs.result.GetAllCurrentUserBankingCardsResult;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GetAllCurrentUserBankingCards {
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;
    private final BankingCardRepository bankingCardRepository;

    public GetAllCurrentUserBankingCards(
        AuthenticationContext authenticationContext,
        PasswordValidator passwordValidator,
        BankingCardRepository bankingCardRepository
    ) {
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
        this.bankingCardRepository = bankingCardRepository;
    }

    /**
     * Get all banking cards of the current user.
     *
     * @param query
     * @return
     */
    public GetAllCurrentUserBankingCardsResult execute(GetAllCurrentUserCardsQuery query) {
        // Current user
        final User currentUser = authenticationContext.getCurrentUser();

        Set<BankingCard> bankingCards = bankingCardRepository.findCardsByUserId(currentUser.getId());

        return new GetAllCurrentUserBankingCardsResult(
            bankingCards.stream()
                .map(BankingCardResult::from)
                .collect(Collectors.toSet())
        );
    }
}