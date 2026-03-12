package com.damian.xBank.shared.infrastructure.web.dto.response;

import com.damian.xBank.modules.banking.transaction.application.dto.BankingTransactionResult;
import com.damian.xBank.modules.banking.transaction.domain.model.BankingTransaction;
import com.damian.xBank.modules.banking.transaction.infrastructure.mapper.BankingTransactionDtoMapper;
import com.damian.xBank.modules.notification.application.dto.NotificationResult;
import com.damian.xBank.modules.notification.domain.model.Notification;
import com.damian.xBank.modules.notification.infrastructure.rest.mapper.NotificationDtoMapper;
import org.springframework.data.domain.Page;

import java.util.List;

public record PageResult<T>(
    List<T> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages
) {
    public static PageResult<BankingTransactionResult> fromPagedTransactions(Page<BankingTransaction> pagedResult) {
        return new PageResult<>(
            pagedResult.getContent().stream()
                .map(BankingTransactionDtoMapper::toBankingTransactionResult)
                .toList(),
            pagedResult.getPageable().getPageNumber(),
            pagedResult.getPageable().getPageSize(),
            pagedResult.getTotalElements(),
            pagedResult.getTotalPages()
        );
    }

    public static PageResult<NotificationResult> fromPagedNotifications(Page<Notification> pagedResult) {
        return new PageResult<>(
            pagedResult.getContent().stream()
                .map(NotificationDtoMapper::toResult)
                .toList(),
            pagedResult.getPageable().getPageNumber(),
            pagedResult.getPageable().getPageSize(),
            pagedResult.getTotalElements(),
            pagedResult.getTotalPages()
        );
    }
}