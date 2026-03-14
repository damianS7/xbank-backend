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
     * Create a new card and associate to the account
     *
     * @param bankingAccount
     * @param cardType
     * @return
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
