package com.damian.xBank.modules.banking.account.application.usecase.request;

import com.damian.xBank.modules.banking.account.domain.exception.BankingAccountNotFoundException;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.infrastructure.repository.BankingAccountRepository;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.service.BankingCardDomainService;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import com.damian.xBank.modules.user.user.domain.model.User;
import com.damian.xBank.shared.security.AuthenticationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso donde se pide una nueva tarjeta asociada a una cuenta.
 */
@Service
public class RequestCard {
    private static final int MAX_RETRY_ATTEMPS = 5;
    private final BankingAccountRepository bankingAccountRepository;
    private final AuthenticationContext authenticationContext;
    private final BankingCardDomainService bankingCardDomainService;
    private final BankingCardRepository bankingCardRepository;

    public RequestCard(
        BankingAccountRepository bankingAccountRepository,
        AuthenticationContext authenticationContext,
        BankingCardDomainService bankingCardDomainService,
        BankingCardRepository bankingCardRepository
    ) {
        this.bankingAccountRepository = bankingAccountRepository;
        this.authenticationContext = authenticationContext;
        this.bankingCardDomainService = bankingCardDomainService;
        this.bankingCardRepository = bankingCardRepository;
    }

    /**
     * @param command Comando con los datos requeridos
     * @return La tarjeta creada
     */
    @Transactional
    public RequestCardResult execute(RequestCardCommand command) {
        // Usuario actual
        final User currentUser = authenticationContext.getCurrentUser();

        // La cuenta a la que se asociara la tarjeta
        final BankingAccount bankingAccount = bankingAccountRepository
            .findById(command.bankingAccountId())
            .orElseThrow(
                () -> new BankingAccountNotFoundException(command.bankingAccountId())
            );

        // Si no es admin ...
        if (!currentUser.isAdmin()) {
            // Comprobar que el owner de la cuenta
            bankingAccount.assertOwnedBy(currentUser.getId());
        }

        // TODO usar issueCard desde el banking account
        BankingCard card = bankingCardDomainService.createBankingCard(bankingAccount, command.type());
        int retry = 0;

        do {
            try {
                bankingCardRepository.saveAndFlush(card);
                break;
            } catch (DataIntegrityViolationException exception) {
                card = bankingCardDomainService.createBankingCard(bankingAccount, command.type());
                retry++;
            }
        } while (retry < MAX_RETRY_ATTEMPS);

        return RequestCardResult.from(card);
    }
}