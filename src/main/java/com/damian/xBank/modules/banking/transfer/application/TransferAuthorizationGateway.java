package com.damian.xBank.modules.banking.transfer.application;

import com.damian.xBank.modules.banking.transfer.infrastructure.rest.dto.request.TransferAuthorizationRequest;
import com.damian.xBank.modules.banking.transfer.infrastructure.rest.dto.response.TransferAuthorizationResponse;

public interface TransferAuthorizationGateway {
    TransferAuthorizationResponse authorizeTransfer(TransferAuthorizationRequest request);
}
