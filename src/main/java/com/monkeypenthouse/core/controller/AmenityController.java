package com.monkeypenthouse.core.controller;

import com.monkeypenthouse.core.component.CommonResponseMaker;
import com.monkeypenthouse.core.component.CommonResponseMaker.CommonResponseEntity;
import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.dto.GetPageResponseDTO;
import com.monkeypenthouse.core.dto.AmenityDTO.*;
import com.monkeypenthouse.core.dto.GetTicketsOfAmenityResponseDto;
import com.monkeypenthouse.core.dto.GetViewedResponseDTO;
import com.monkeypenthouse.core.service.AmenityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/amenity")
@Log4j2
@RequiredArgsConstructor
public class AmenityController {
    private final AmenityService amenityService;
    private final CommonResponseMaker commonResponseMaker;

    @PostMapping
    public CommonResponseEntity save(@ModelAttribute @Valid SaveReqDTO amenityDTO) throws Exception {
        amenityService.add(amenityDTO);

        return commonResponseMaker.makeCommonResponse(ResponseCode.SUCCESS);
    }

    @GetMapping(value = "/{id}")
    public CommonResponseEntity getById(@PathVariable("id") Long id) throws Exception {

        return commonResponseMaker.makeCommonResponse(amenityService.getById(id), ResponseCode.SUCCESS);
    }

    @GetMapping(value = "/recently")
    public CommonResponseEntity getPage(Pageable pageable) throws Exception {

        final GetPageResponseDTO responseDto =
                GetPageResponseDTO.of(amenityService.getPage(pageable));
        return commonResponseMaker.makeCommonResponse(responseDto, ResponseCode.SUCCESS);
    }

    @GetMapping(value = "/category")
    public CommonResponseEntity getPageByCategory(@RequestParam("category") Long category, Pageable pageable) throws Exception {
        final GetPageResponseDTO responseDto =
                GetPageResponseDTO.of(amenityService.getPageByCategory(category, pageable));

        return commonResponseMaker.makeCommonResponse(responseDto, ResponseCode.SUCCESS);
    }

    @GetMapping(value = "/recommended")
    public CommonResponseEntity getPageByRecommended(Pageable pageable) throws Exception {

        final GetPageResponseDTO responseDto =
                GetPageResponseDTO.of(amenityService.getPageByRecommended(pageable));

        return commonResponseMaker.makeCommonResponse(responseDto, ResponseCode.SUCCESS);
    }

    @GetMapping(value = "/dibs")
    public CommonResponseEntity getPageByDibsOn(
            @AuthenticationPrincipal final UserDetails userDetails,
            Pageable pageable) throws Exception {

        final GetPageResponseDTO responseDTO = GetPageResponseDTO.of(amenityService.getAmenitiesDibsOn(userDetails, pageable));

        return commonResponseMaker.makeCommonResponse(responseDTO, ResponseCode.SUCCESS);
    }

    @GetMapping(value = "/{id}/tickets")
    public CommonResponseEntity getTicketsOfAmenity(@PathVariable("id") final Long amenityId) {

        final GetTicketsOfAmenityResponseDto responseDto =
                GetTicketsOfAmenityResponseDto.of(amenityService.getTicketsOfAmenity(amenityId));

        return commonResponseMaker.makeCommonResponse(responseDto, ResponseCode.SUCCESS);
    }

    @GetMapping(value = "/viewed")
    public CommonResponseEntity getViewed(@RequestParam("id") final List<Long> amentiyIds) throws Exception {

        final GetViewedResponseDTO responseDto =
                GetViewedResponseDTO.of(amenityService.getViewed(amentiyIds));

        return commonResponseMaker.makeCommonResponse(responseDto, ResponseCode.SUCCESS);
    }
}
