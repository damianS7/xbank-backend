package com.damian.xBank.modules.banking.transfer.outgoing.application;

import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.request.TransferAuthorizationRequest;
import com.damian.xBank.modules.banking.transfer.outgoing.infrastructure.rest.response.TransferAuthorizationResponse;

public interface TransferAuthorizationGateway {
    TransferAuthorizationResponse authorizeTransfer(TransferAuthorizationRequest request);
}
