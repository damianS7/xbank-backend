package com.damian.xBank.modules.payment.checkout.domain;

import com.damian.xBank.modules.payment.intent.domain.model.PaymentIntentStatus;

import java.math.BigDecimal;

public class PaymentCheckoutForm {
    private Long paymentId;
    private boolean isPending;
    private PaymentIntentStatus status;
    private String merchant;
    private String merchantCallbackUrl;
    private BigDecimal amount;
    private String cardHolder;
    private String cardNumber;
    private String cardPin;
    private String cvv;
    private int expiryMonth;
    private int expiryYear;

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

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }

    public PaymentIntentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentIntentStatus status) {
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
               + ", status=" + status
               + ", merchant=" + merchant
               + ", amount=" + amount
               + ", cardNumber=" + cardNumber
               + ", cardPin=" + cardNumber
               + ", cardCvv=" + cvv
               + "}";
    }

    public String getMerchantCallbackUrl() {
        return merchantCallbackUrl;
    }

    public void setMerchantCallbackUrl(String merchantCallbackUrl) {
        this.merchantCallbackUrl = merchantCallbackUrl;
    }

    public int getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(int expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public int getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(int expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }
}