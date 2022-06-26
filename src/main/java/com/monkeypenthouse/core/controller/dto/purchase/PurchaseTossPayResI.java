package com.monkeypenthouse.core.controller.dto.purchase;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseTossPayResI {

    public String mId;
    public String version;
    public String paymentKey;
    public String orderId;
    public String orderName;
    public String currency;
    public String method;
    public Integer totalAmount;
    public Integer balanceAmount;
    public Integer suppliedAmount;
    public Integer vat;
    public String status;
    public String requestedAt;
    public String approvedAt;
    public Boolean useEscrow;
    public Boolean cultureExpense;
    public String virtualAccount;
    public String transfer;
    public String mobilePhone;
    public String giftCertificate;
    public String cashReceipt;
    public String discount;
    public List<Cancel> cancels;
    public String secret;
    public String type;
    public String easyPay;
    public Integer taxFreeAmount;
    public Card card;

    @Data
    public static class Card {
        public String company;
        public String number;
        public Integer installmentPlanMonths;
        public Boolean isInterestFree;
        public String approveNo;
        public Boolean useCardPoint;
        public String cardType;
        public String ownerType;
        public String acquireStatus;
        public String receiptUrl;
    }

    @Data
    public static class Cancel {
        public Integer cancelAmount;
        public String cancelReason;
        public Integer taxFreeAmount;
        public Integer taxAmount;
        public Integer refundableAmount;
        public String canceledAt;
        public String transactionKey;
    }

}