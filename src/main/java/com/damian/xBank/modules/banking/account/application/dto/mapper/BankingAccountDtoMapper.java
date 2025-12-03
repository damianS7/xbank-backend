package com.damian.xBank.modules.banking.account.application.dto.mapper;

import com.damian.xBank.modules.banking.account.application.dto.response.BankingAccountDetailDto;
import com.damian.xBank.modules.banking.account.application.dto.response.BankingAccountDto;
import com.damian.xBank.modules.banking.account.application.dto.response.BankingAccountSummaryDto;
import com.damian.xBank.modules.banking.account.domain.entity.BankingAccount;
import com.damian.xBank.modules.banking.card.application.dto.mapper.BankingCardDtoMapper;
import com.damian.xBank.modules.banking.card.application.dto.response.BankingCardDto;
import com.damian.xBank.modules.banking.transaction.application.dto.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.banking.transaction.application.dto.response.BankingTransactionDto;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class BankingAccountDtoMapper {
    public static BankingAccountDto toBankingAccountDto(BankingAccount bankingAccount) {
        Set<BankingCardDto> bankingCardsDto = Collections.emptySet();
        //        Set<BankingCardDto> bankingCardsDTO = Optional
        //                .ofNullable(bankingAccount.getBankingCards())
        //                .orElseGet(Collections::emptySet)
        //                .stream()
        //                .map(BankingCardDTOMapper::toBankingCardDTO)
        //                .collect(Collectors.toSet());

        Set<BankingTransactionDto> bankingTransactionsDto = Collections.emptySet();
        //                Optional
        //                .ofNullable(bankingAccount.getAccountTransactions())
        //                .orElseGet(Collections::emptySet)
        //                .stream()
        //                .map(BankingTransactionDTOMapper::toBankingTransactionDTO)
        //                .collect(Collectors.toSet());

        return new BankingAccountDto(
                bankingAccount.getId(),
                bankingAccount.getAlias(),
                bankingAccount.getAccountNumber(),
                bankingAccount.getBalance(),
                bankingAccount.getAccountType(),
                bankingAccount.getAccountCurrency(),
                bankingAccount.getAccountStatus(),
                bankingTransactionsDto,
                bankingCardsDto,
                bankingAccount.getCreatedAt(),
                bankingAccount.getUpdatedAt()
        );
    }

    public static BankingAccountSummaryDto toBankingAccountSummaryDto(BankingAccount bankingAccount) {
        return new BankingAccountSummaryDto(
                bankingAccount.getId(),
                bankingAccount.getAlias(),
                bankingAccount.getAccountNumber(),
                bankingAccount.getBalance(),
                bankingAccount.getAccountType(),
                bankingAccount.getAccountCurrency(),
                bankingAccount.getAccountStatus(),
                (long) bankingAccount.getBankingCards().size(),
                bankingAccount.getCreatedAt(),
                bankingAccount.getUpdatedAt()
        );
    }

    public static Set<BankingAccountSummaryDto> toBankingAccountSummaryDtoSet(Set<BankingAccount> bankingAccounts) {
        return bankingAccounts.stream().map(
                BankingAccountDtoMapper::toBankingAccountSummaryDto
        ).collect(Collectors.toSet());
    }

    public static BankingAccountDetailDto toBankingAccountDetailDto(BankingAccount bankingAccount) {

        Page<BankingTransactionDto> bankingAccountTransactions = BankingTransactionDtoMapper
                .toBankingTransactionPageDto(
                        bankingAccount.getAccountTransactions()
                );

        Set<BankingCardDto> bankingCardsDto = Optional
                .ofNullable(bankingAccount.getBankingCards())
                .orElseGet(Collections::emptySet)
                .stream()
                .map(BankingCardDtoMapper::toBankingCardDto)
                .collect(Collectors.toSet());

        return new BankingAccountDetailDto(
                bankingAccount.getId(),
                bankingAccount.getAlias(),
                bankingAccount.getAccountNumber(),
                bankingAccount.getBalance(),
                bankingAccount.getAccountType(),
                bankingAccount.getAccountCurrency(),
                bankingAccount.getAccountStatus(),
                bankingAccountTransactions,
                bankingCardsDto,
                bankingAccount.getCreatedAt(),
                bankingAccount.getUpdatedAt()
        );
    }

    public static Set<BankingAccountDto> toBankingAccountSetDto(Set<BankingAccount> bankingAccounts) {
        return bankingAccounts.stream().map(
                BankingAccountDtoMapper::toBankingAccountDto
        ).collect(Collectors.toSet());
    }
}
