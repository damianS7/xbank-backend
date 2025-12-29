package com.damian.xBank.modules.banking.account.infrastructure.web.controller;

import com.damian.xBank.modules.banking.account.application.service.BankingAccountOperationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class BankingAccountOperationController {
    private final BankingAccountOperationService bankingAccountOperationService;

    public BankingAccountOperationController(
            BankingAccountOperationService bankingAccountOperationService
    ) {
        this.bankingAccountOperationService = bankingAccountOperationService;
    }

}

