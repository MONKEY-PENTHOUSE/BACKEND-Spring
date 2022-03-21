package com.monkeypenthouse.core.controller;

import com.monkeypenthouse.core.common.DefaultRes;
import com.monkeypenthouse.core.common.ResponseMessage;
import com.monkeypenthouse.core.dto.AmenityDTO;
import com.monkeypenthouse.core.dto.CompleteOrderRequestDto;
import com.monkeypenthouse.core.exception.DataNotFoundException;
import com.monkeypenthouse.core.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/order")
@Log4j2
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping(value = "/complete")
    public ResponseEntity<DefaultRes<?>> completeOrder(@RequestBody final CompleteOrderRequestDto requestDto) throws DataNotFoundException, IOException, InterruptedException {
        orderService.completeOrder(requestDto.toVo());
        return new ResponseEntity<>(
                DefaultRes.res(
                        HttpStatus.OK.value(),
                        ResponseMessage.PAYMENT_APPROVED),
                HttpStatus.OK
        );
    }
}
