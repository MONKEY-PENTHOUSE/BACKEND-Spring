package com.monkeypenthouse.core.service;

//import ...

import com.monkeypenthouse.core.entity.Ticket;
import com.monkeypenthouse.core.repository.TicketRepository;
import com.monkeypenthouse.core.vo.TicketOfOrderVo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class OrderServiceImpl implements OrderService{

    @Autowired
//    TicketRepository ticketRepository;

    @Override
    @Transactional
    public Optional<Ticket> getTicketInfo(List<TicketOfOrderVo> ticketOfOrderVoList) {
//        ArrayList idList = new ArrayList();
//        for(int i=0; i<ticketOfOrderVoList.size();i++) {
//            idList.add(ticketOfOrderVoList.get(i).getId());
//        }
        log.info(ticketOfOrderVoList);
        return (null);
//        Optional<Ticket> ticketInfo = ticketRepository.findAllById(idList);
//        log.info(ticketInfo);
//        return (ticketInfo);
    }
//        return ticketRepository.findAllById(ids);
//        Optional<Room> roomOptional = roomRepository.findByUserId(user.getId());
//        Room room = roomOptional.orElseThrow(() -> new RuntimeException("빈 방이 없습니다."));
//        userRepository.updateRoomId(user.getId(), room);
//        return room;
//    }
//    public OrderServiceImpl(TicketRepository ticketRepository) {
//        this.ticketRepository = ticketRepository;
//    }
}
