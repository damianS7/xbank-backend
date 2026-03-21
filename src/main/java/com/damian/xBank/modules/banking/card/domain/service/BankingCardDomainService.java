package com.damian.xBank.modules.banking.card.domain.service;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardType;
import org.springframework.stereotype.Service;

@Service
public class BankingCardDomainService {
    private final BankingCardGenerator bankingCardGenerator;

    public BankingCardDomainService(
        BankingCardGenerator bankingCardGenerator
    ) {
        this.bankingCardGenerator = bankingCardGenerator;
    }

    /**
     * Crea una tarjeta nueva asociada a una cuenta.
     *
     * @param bankingAccount Cuenta a la que se asocia la tarjeta
     * @param cardType       Tipo de tarjeta
     * @return La tarjeta creada
     */
    public BankingCard createBankingCard(
        BankingAccount bankingAccount,
        BankingCardType cardType
    ) {
        return bankingAccount.issueCard(
            cardType,
            bankingCardGenerator.generateCardNumber(),
            bankingCardGenerator.generateCvv(),
            bankingCardGenerator.generatePin()
        );
    }
}
