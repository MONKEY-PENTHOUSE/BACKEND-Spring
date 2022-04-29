package com.monkeypenthouse.core.controller;

import com.monkeypenthouse.core.component.CommonResponseMaker;
import com.monkeypenthouse.core.component.CommonResponseMaker.CommonResponseEntity;
import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.dto.ApprovePurchaseRequestDto;
import com.monkeypenthouse.core.dto.CancelPurchaseRequestDto;
import com.monkeypenthouse.core.dto.CreatePurchaseRequestDto;
import com.monkeypenthouse.core.dto.CreatePurchaseResponseDto;
import com.monkeypenthouse.core.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/purchase")
@Log4j2
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final CommonResponseMaker commonResponseMaker;

    @PostMapping(value = "/create")
    public CommonResponseEntity createPurchase(@AuthenticationPrincipal final UserDetails userDetails,
                                               @RequestBody final CreatePurchaseRequestDto requestDto) throws Exception {

        final CreatePurchaseResponseDto responseDto =
                CreatePurchaseResponseDto.of(purchaseService.createPurchase(userDetails, requestDto.toVo()));

        return commonResponseMaker.makeCommonResponse(responseDto, ResponseCode.SUCCESS);
    }

    @PostMapping(value = "/approve")
    public CommonResponseEntity approvePurchase(@RequestBody final ApprovePurchaseRequestDto requestDto) throws IOException, InterruptedException {

        purchaseService.approvePurchase(requestDto.toVo());

        return commonResponseMaker.makeCommonResponse(ResponseCode.SUCCESS);
    }

    @PostMapping(value = "/cancel")
    public CommonResponseEntity cancelPurchase(@RequestBody final CancelPurchaseRequestDto requestDto) throws IOException, InterruptedException {

        purchaseService.cancelPurchase(requestDto.toVo());

        return commonResponseMaker.makeCommonResponse(ResponseCode.SUCCESS);
    }
}
