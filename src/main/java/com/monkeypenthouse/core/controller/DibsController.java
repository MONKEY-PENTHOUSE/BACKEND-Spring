package com.monkeypenthouse.core.controller;

import com.monkeypenthouse.core.common.DefaultRes;
import com.monkeypenthouse.core.common.ResponseMessage;
import com.monkeypenthouse.core.component.CommonResponseMaker;
import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.dto.CommonResponse;
import com.monkeypenthouse.core.exception.DataNotFoundException;
import com.monkeypenthouse.core.security.PrincipalDetails;
import com.monkeypenthouse.core.service.DibsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Log4j2
@RequiredArgsConstructor
public class DibsController {

    private final DibsService dibsService;
    private final CommonResponseMaker commonResponseMaker;

    /**
     * Create Dibs (찜하기 추가)
     */
    @PostMapping("/dibs")
    public CommonResponse<Void> createDibs(
            @AuthenticationPrincipal final UserDetails userDetails, @RequestParam final Long amenityId) throws DataNotFoundException {

        dibsService.createDibs(userDetails, amenityId);

        return commonResponseMaker.makeEmptyInfoCommonResponse(ResponseCode.SUCCESS);
    }

    /**
     * Delete Dibs (찜하기 제거)
     */
    @DeleteMapping("/dibs")
    public CommonResponse<Void> deleteDibs(
            @AuthenticationPrincipal final UserDetails userDetails, @RequestParam final Long amenityId) throws DataNotFoundException {

        dibsService.deleteDibs(userDetails, amenityId);

        return commonResponseMaker.makeEmptyInfoCommonResponse(ResponseCode.SUCCESS);
    }
}
