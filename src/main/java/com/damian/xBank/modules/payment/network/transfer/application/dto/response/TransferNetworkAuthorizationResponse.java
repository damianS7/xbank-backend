package com.damian.xBank.modules.payment.network.transfer.application.dto.response;

public record TransferNetworkAuthorizationResponse(
    String authorizationId,
    String status,
    String declineReason
) {
}