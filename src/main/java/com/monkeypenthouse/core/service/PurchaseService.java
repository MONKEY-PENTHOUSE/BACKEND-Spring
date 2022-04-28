package com.monkeypenthouse.core.service;


import com.monkeypenthouse.core.vo.ApproveOrderRequestVo;
import com.monkeypenthouse.core.vo.CreateOrderRequestVo;
import com.monkeypenthouse.core.vo.CreateOrderResponseVo;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

public interface PurchaseService {

    CreateOrderResponseVo createPurchase(final UserDetails userDetails, final CreateOrderRequestVo requestVo) throws InterruptedException;

    void approvePurchase(ApproveOrderRequestVo requestVo) throws IOException, InterruptedException;
}
