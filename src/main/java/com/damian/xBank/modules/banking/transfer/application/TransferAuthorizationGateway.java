package com.damian.xBank.modules.banking.transfer.application;

import com.damian.xBank.modules.banking.transfer.infrastructure.rest.request.TransferAuthorizationRequest;
import com.damian.xBank.modules.banking.transfer.infrastructure.rest.response.TransferAuthorizationResponse;

public interface TransferAuthorizationGateway {
    TransferAuthorizationResponse authorizeTransfer(TransferAuthorizationRequest request);
}
