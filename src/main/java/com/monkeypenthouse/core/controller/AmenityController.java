package com.monkeypenthouse.core.controller;

import com.monkeypenthouse.core.common.DefaultRes;
import com.monkeypenthouse.core.common.ResponseMessage;
import com.monkeypenthouse.core.dao.User;
import com.monkeypenthouse.core.dto.AmenityDTO;
import com.monkeypenthouse.core.dto.AmenityDTO.*;
import com.monkeypenthouse.core.exception.DataNotFoundException;
import com.monkeypenthouse.core.service.AmenityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/amenity")
@Log4j2
@RequiredArgsConstructor
public class AmenityController {
    private final AmenityService amenityService;
    private final ModelMapper modelMapper;

    @PostMapping(value = "/")
    public ResponseEntity<DefaultRes<?>> signUp(
            @RequestPart(value = "bannerPhotos", required = false) List<MultipartFile> bannerPhotos,
            @RequestPart(value = "detailPhotos", required = false) List<MultipartFile> detailPhotos,
            @RequestPart(value = "saveReqDTO") @Valid SaveReqDTO amenityDTO) throws Exception {
        amenityService.add(bannerPhotos, detailPhotos, amenityDTO);
        return new ResponseEntity<>(
                DefaultRes.res(
                        HttpStatus.CREATED.value(),
                        ResponseMessage.CREATED_AMENITY),
                HttpStatus.CREATED
        );
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DefaultRes<?>> signUp(@PathVariable("id") Long id) throws Exception {
        return new ResponseEntity<>(
                DefaultRes.res(
                        HttpStatus.OK.value(),
                        ResponseMessage.READ_INFO,
                        amenityService.getById(id)),
                HttpStatus.OK
        );
    }

    @GetMapping(value = "/dibs")
    public ResponseEntity<DefaultRes<?>> getAmenitiesDibsOn(
            @AuthenticationPrincipal final UserDetails userDetails) throws DataNotFoundException {

        List<DetailDTO> amenityDTOList = amenityService.getAmenitiesDibsOn(userDetails)
                .stream()
                .map(amenity -> modelMapper.map(amenity,DetailDTO.class))
                .collect(Collectors.toList());

        return new ResponseEntity<>(
                DefaultRes.res(
                        HttpStatus.OK.value(),
                        ResponseMessage.GET_AMENITY_DIBS_ON,
                        amenityDTOList),
                HttpStatus.OK
        );
    }
}
