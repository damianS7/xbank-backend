package com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.response;

public record TransferNetworkAuthorizationResponse(
    String authorizationId,
    String status,
    String declineReason
) {
}