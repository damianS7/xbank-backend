package com.damian.xBank.modules.banking.account.application.dto.request;

import com.damian.xBank.modules.banking.account.domain.model.BankingAccountStatus;
import jakarta.validation.constraints.NotNull;

public record BankingAccountStatusRequest(

        @NotNull(message = "Status must not be null")
        BankingAccountStatus status
) {

}
