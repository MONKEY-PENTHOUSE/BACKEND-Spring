package com.monkeypenthouse.core.controller;

import com.monkeypenthouse.core.component.CommonResponseMaker;
import com.monkeypenthouse.core.component.CommonResponseMaker.CommonResponseEntity;
import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.dto.ApproveOrderRequestDto;
import com.monkeypenthouse.core.dto.CreateOrderRequestDto;
import com.monkeypenthouse.core.dto.CreateOrderResponseDto;
import com.monkeypenthouse.core.service.OrderService;
import com.monkeypenthouse.core.vo.CreateOrderResponseVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/order")
@Log4j2
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CommonResponseMaker commonResponseMaker;

    @PostMapping(value = "/create")
    public CommonResponseEntity createOrder(@AuthenticationPrincipal final UserDetails userDetails,
                                            @RequestBody final CreateOrderRequestDto requestDto) throws Exception {

        final CreateOrderResponseDto responseDto =
                CreateOrderResponseDto.of(orderService.createOrder(userDetails, requestDto.toVo()));

        return commonResponseMaker.makeCommonResponse(responseDto, ResponseCode.SUCCESS);
    }

    @PostMapping(value = "/approve")
    public CommonResponseEntity approveOrder(@RequestBody final ApproveOrderRequestDto requestDto) throws IOException, InterruptedException {

        orderService.approveOrder(requestDto.toVo());

        return commonResponseMaker.makeCommonResponse(ResponseCode.SUCCESS);
    }
}
