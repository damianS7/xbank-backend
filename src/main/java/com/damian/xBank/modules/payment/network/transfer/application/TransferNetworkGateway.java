package com.damian.xBank.modules.payment.network.transfer.application;

import com.damian.xBank.modules.payment.network.transfer.application.dto.request.TransferNetworkAuthorizationRequest;
import com.damian.xBank.modules.payment.network.transfer.application.dto.response.TransferNetworkAuthorizationResponse;

public interface TransferNetworkGateway {
    TransferNetworkAuthorizationResponse authorizeTransfer(TransferNetworkAuthorizationRequest request);
}
