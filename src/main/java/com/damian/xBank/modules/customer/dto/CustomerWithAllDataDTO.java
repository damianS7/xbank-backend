package com.damian.xBank.modules.customer.dto;

import com.damian.xBank.modules.banking.account.BankingAccountDTO;
import com.damian.xBank.modules.customer.CustomerRole;
import com.damian.xBank.modules.customer.profile.ProfileDTO;

import java.time.Instant;
import java.util.Set;

public record CustomerWithAllDataDTO(
        Long id,
        String email,
        CustomerRole role,
        ProfileDTO profile,
        Set<BankingAccountDTO> bankingAccounts,
        Instant createdAt,
        Instant updatedAt
) {
}