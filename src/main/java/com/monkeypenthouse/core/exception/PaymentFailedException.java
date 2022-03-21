package com.monkeypenthouse.core.exception;

public class PaymentFailedException extends RuntimeException{

    public PaymentFailedException() {
        super("결제가 승인되지 않았습니다.");
    }
}
