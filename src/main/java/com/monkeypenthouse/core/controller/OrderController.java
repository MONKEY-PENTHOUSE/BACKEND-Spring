package com.monkeypenthouse.core.controller;


import com.monkeypenthouse.core.common.DefaultRes;
import com.monkeypenthouse.core.service.OrderService;
import com.monkeypenthouse.core.vo.TicketOfOrderVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@Log4j2
@RequestMapping("/order")
public class OrderController{

    private final OrderService orderService;

//    public OrderController(OrderService orderService) {
//        this.orderService = orderService;
//    }

    @PostMapping("/order-form")
    public ResponseEntity<DefaultRes<?>> orderForm(@RequestBody List<TicketOfOrderVo> ticketOfOrderVoList) throws Exception {

        orderService.getTicketInfo(ticketOfOrderVoList);

//        final OrderServiceImpl orderService = null;
//                log.info(ticketOfOrderVoList.size());
//        ArrayList idList = new ArrayList();
//        for(int i=0; i<ticketOfOrderVoList.size();i++) {
//            idList.add(ticketOfOrderVoList.get(i).getId());
//        }
//        log.info(orderService.getTicketInfo(idList));

        return new ResponseEntity<>(
                DefaultRes.res(HttpStatus.OK.value(), "hello world!"),
                HttpStatus.OK
        );
    }
//    public OrderVo order(@RequestBody OrderVo orderVo) {
//        return orderVo;
//    }
//    public ResponseEntity<DefaultRes<?>> home(@RequestBody OrderVo orderVo) {
//        return orderVo;
//                new ResponseEntity<>(
//                log.info(orderVo);
//                DefaultRes.res(HttpStatus.OK.value(), "hello order!"),
//                HttpStatus.OK
//        );
//    }

//    public ResponseEntity<DefaultRes<?>> home(@RequestBody OrderDTO.DetailDTO orderInfo) {
//        log.info(orderInfo);
//        log.info(HttpStatus.OK.value());
//        return orderInfo.getOrderInfo()
//        return "test"
//                new ResponseEntity<>(
//                DefaultRes.res(HttpStatus.OK.value(), "hello order!"),
//                HttpStatus.OK
//        );
//    }

//    @GetMapping(value = "/{id}")
//    public ResponseEntity<DefaultRes<?>> getById(@PathVariable("id") Long id) throws Exception {
//        return new ResponseEntity<>(
//                DefaultRes.res(
//                        HttpStatus.OK.value(),
//                        ResponseMessage.READ_INFO,
//
////                        amenityService.getById(id)),
//                HttpStatus.OK
//        );
//    }
}

