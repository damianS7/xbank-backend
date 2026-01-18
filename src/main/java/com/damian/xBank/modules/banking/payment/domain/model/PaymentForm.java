package com.damian.xBank.modules.banking.payment.domain.model;

import java.math.BigDecimal;

public class PaymentForm {
    private Long paymentId;
    private Long invoiceId;
    private boolean isPending;
    private String status;
    private String merchant;
    private BigDecimal amount;

    private String cardNumber;
    private String cardPin;
    private String cvv;

    // Getters y setters
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardPin() {
        return cardPin;
    }

    public void setCardPin(String cardPin) {
        this.cardPin = cardPin;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    @Override
    public String toString() {
        return "PaymentForm{"
               + "paymentId=" + paymentId
               + ", invoiceId=" + invoiceId
               + ", status=" + status
               + ", merchant=" + merchant
               + ", amount=" + amount
               + ", cardNumber=" + cardNumber
               + ", cardPin=" + cardNumber
               + ", cardCvv=" + cvv
               + "}";
    }
}