package com.monkeypenthouse.core.service;


import com.monkeypenthouse.core.vo.ApproveOrderRequestVo;
import com.monkeypenthouse.core.vo.CreateOrderRequestVo;
import com.monkeypenthouse.core.vo.CreateOrderResponseVo;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

public interface OrderService {

    CreateOrderResponseVo createOrder(final UserDetails userDetails, final CreateOrderRequestVo requestVo);

    void approveOrder(ApproveOrderRequestVo requestVo) throws IOException, InterruptedException;
}
