package com.monkeypenthouse.core.service;


import com.monkeypenthouse.core.vo.ApproveOrderRequestVo;
import com.monkeypenthouse.core.vo.CancelPurchaseRequestVo;
import com.monkeypenthouse.core.vo.CreatePurchaseRequestVo;
import com.monkeypenthouse.core.vo.CreateOrderResponseVo;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

public interface PurchaseService {

    CreateOrderResponseVo createPurchase(final UserDetails userDetails, final CreatePurchaseRequestVo requestVo) throws InterruptedException;

    void approvePurchase(ApproveOrderRequestVo requestVo) throws IOException, InterruptedException;

    void loadPurchaseDataOnRedis();

    void cancelPurchase(CancelPurchaseRequestVo requestVo);
}
