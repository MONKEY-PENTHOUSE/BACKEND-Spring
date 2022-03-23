package com.monkeypenthouse.core.controller;

import com.monkeypenthouse.core.component.CommonResponseMaker;
import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.dto.GetPageResponseDTO;
import com.monkeypenthouse.core.dto.AmenityDTO.*;
import com.monkeypenthouse.core.dto.GetTicketsOfAmenityResponseDto;
import com.monkeypenthouse.core.service.AmenityService;
import com.monkeypenthouse.core.vo.GetByIdResponseVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/amenity")
@Log4j2
@RequiredArgsConstructor
public class AmenityController {
    private final AmenityService amenityService;
    private final ModelMapper modelMapper;
    private final CommonResponseMaker commonResponseMaker;

    @PostMapping(value = "/")
    public CommonResponseMaker.CommonResponse<Void> signUp(
            @RequestPart(value = "bannerPhotos", required = false) List<MultipartFile> bannerPhotos,
            @RequestPart(value = "detailPhotos", required = false) List<MultipartFile> detailPhotos,
            @RequestPart(value = "saveReqDTO") @Valid SaveReqDTO amenityDTO) throws Exception {
        amenityService.add(bannerPhotos, detailPhotos, amenityDTO);

        return commonResponseMaker.makeEmptyInfoCommonResponse(ResponseCode.SUCCESS);
    }

    @GetMapping(value = "/{id}")
    public CommonResponseMaker.CommonResponse<GetByIdResponseVo> getById(@PathVariable("id") Long id) throws Exception {

        return commonResponseMaker.makeSucceedCommonResponse(amenityService.getById(id));
    }

    @GetMapping(value = "/recently")
    public CommonResponseMaker.CommonResponse<GetPageResponseDTO> getPage(Pageable pageable) throws Exception {

        final GetPageResponseDTO responseDto =
                GetPageResponseDTO.of(amenityService.getPage(pageable));
        return commonResponseMaker.makeSucceedCommonResponse(responseDto);
    }

    @GetMapping(value = "/category")
    public CommonResponseMaker.CommonResponse<GetPageResponseDTO> getPageByCategory(@Param("category") Long category, Pageable pageable) throws Exception {
        final GetPageResponseDTO responseDto =
                GetPageResponseDTO.of(amenityService.getPageByCategory(category, pageable));

        return commonResponseMaker.makeSucceedCommonResponse(responseDto);
    }

    @GetMapping(value = "/recommended")
    public CommonResponseMaker.CommonResponse<GetPageResponseDTO> getPageByRecommended(Pageable pageable) throws Exception {

        final GetPageResponseDTO responseDto =
                GetPageResponseDTO.of(amenityService.getPageByRecommended(pageable));

        return commonResponseMaker.makeSucceedCommonResponse(responseDto);
    }

    @GetMapping(value = "/dibs")
    public CommonResponseMaker.CommonResponse<GetPageResponseDTO> getPageByDibsOn(
            @AuthenticationPrincipal final UserDetails userDetails,
            Pageable pageable) throws Exception {

        final GetPageResponseDTO responseDTO = GetPageResponseDTO.of(amenityService.getAmenitiesDibsOn(userDetails, pageable));

        return commonResponseMaker.makeSucceedCommonResponse(responseDTO);
    }

    @GetMapping(value = "/{id}/tickets")
    public CommonResponseMaker.CommonResponse<GetTicketsOfAmenityResponseDto> getTicketsOfAmenity(@PathVariable("id") final Long amenityId) {

        final GetTicketsOfAmenityResponseDto responseDto =
                GetTicketsOfAmenityResponseDto.of(amenityService.getTicketsOfAmenity(amenityId));

        return commonResponseMaker.makeSucceedCommonResponse(responseDto);
    }
}
