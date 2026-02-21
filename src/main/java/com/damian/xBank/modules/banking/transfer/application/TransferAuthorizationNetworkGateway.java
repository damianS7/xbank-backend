package com.damian.xBank.modules.banking.transfer.application;

import com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.request.TransferNetworkAuthorizationRequest;
import com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.response.TransferNetworkAuthorizationResponse;

public interface TransferAuthorizationNetworkGateway {
    TransferNetworkAuthorizationResponse authorizeTransfer(TransferNetworkAuthorizationRequest request);
}
