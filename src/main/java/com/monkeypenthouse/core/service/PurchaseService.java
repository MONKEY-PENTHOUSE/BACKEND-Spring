package com.monkeypenthouse.core.service;


import com.monkeypenthouse.core.service.dto.purchase.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

public interface PurchaseService {

    PurchaseCreateResS createPurchase(final UserDetails userDetails, final PurchaseCreateReqS params) throws InterruptedException;
    void approvePurchase(final PurchaseApproveReqS params) throws IOException, InterruptedException;
    void cancelPurchase(final PurchaseCancelReqS params);
    void refundPurchase(PurchaseRefundReqS params) throws IOException, InterruptedException;
}
