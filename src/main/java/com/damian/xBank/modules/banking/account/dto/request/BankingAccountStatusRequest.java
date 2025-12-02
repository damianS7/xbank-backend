package com.damian.xBank.modules.banking.account.dto.request;

import com.damian.xBank.modules.banking.account.enums.BankingAccountStatus;
import jakarta.validation.constraints.NotNull;

public record BankingAccountStatusRequest(

        @NotNull(message = "Status must not be null")
        BankingAccountStatus status
) {

}
