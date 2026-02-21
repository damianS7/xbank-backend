package com.damian.xBank.modules.banking.transfer.application;

import com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.request.TransferAuthorizationNetworkRequest;
import com.damian.xBank.modules.banking.transfer.infrastructure.web.dto.response.TransferAuthorizationNetworkResponse;

public interface TransferAuthorizationNetworkGateway {
    TransferAuthorizationNetworkResponse authorizeTransfer(TransferAuthorizationNetworkRequest request);
}
