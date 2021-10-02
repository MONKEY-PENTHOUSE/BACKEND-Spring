package com.monkeypenthouse.core.service;

public interface MessageService {
    void sendSMS(String phoneNum);
    boolean checkAuthNum(String phoneNum, String authNum);
}
