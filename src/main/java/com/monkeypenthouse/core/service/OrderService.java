package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.entity.Ticket;
import com.monkeypenthouse.core.vo.TicketOfOrderVo;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    public Optional<Ticket> getTicketInfo(List<TicketOfOrderVo> ticketOfOrderVoLis);

}
