package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.exception.DataNotFoundException;
import com.monkeypenthouse.core.vo.CompleteOrderRequestVo;

import java.io.IOException;

public interface OrderService {

    void completeOrder(CompleteOrderRequestVo requestVo) throws DataNotFoundException, IOException, InterruptedException;
}
