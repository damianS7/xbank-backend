package com.damian.xBank.modules.banking.account.application.cqrs.result;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccount;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountCurrency;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import com.damian.xBank.modules.banking.account.domain.model.BankingAccountType;
import com.damian.xBank.modules.banking.card.application.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.card.application.mapper.BankingCardDtoMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record BankingAccountResult(
    Long id,
    String alias,
    String accountNumber,
    BigDecimal balance,
    BankingAccountType accountType,
    BankingAccountCurrency accountCurrency,
    BankingAccountStatus accountStatus,
    Set<BankingCardDto> accountCards,
    Instant createdAt,
    Instant updatedAt
) {
    public static BankingAccountResult from(BankingAccount bankingAccount) {
        return new BankingAccountResult(
            bankingAccount.getId(),
            bankingAccount.getAlias(),
            bankingAccount.getAccountNumber(),
            bankingAccount.getBalance(),
            bankingAccount.getType(),
            bankingAccount.getCurrency(),
            bankingAccount.getStatus(),
            BankingCardDtoMapper.toBankingCardSetDTO(bankingAccount.getBankingCards()),
            bankingAccount.getCreatedAt(),
            bankingAccount.getUpdatedAt()
        );
    }
}
