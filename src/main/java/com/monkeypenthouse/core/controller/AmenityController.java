package com.monkeypenthouse.core.controller;

import com.monkeypenthouse.core.common.DefaultRes;
import com.monkeypenthouse.core.common.ResponseMessage;
import com.monkeypenthouse.core.dao.User;
import com.monkeypenthouse.core.dto.AmenityDTO.*;
import com.monkeypenthouse.core.service.AmenityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/amenity")
@Log4j2
@RequiredArgsConstructor
public class AmenityController {
    private final AmenityService amenityService;

    @PostMapping(value = "/")

    public ResponseEntity<DefaultRes<?>> signUp(
            @RequestPart(value="bannerPhotos", required=false) List<MultipartFile> bannerPhotos,
            @RequestPart(value="detailPhotos", required=false) List<MultipartFile> detailPhotos,
            @RequestPart(value = "saveReqDTO") SaveReqDTO amenityDTO) throws Exception {

        return new ResponseEntity<>(
                DefaultRes.res(
                        HttpStatus.CREATED.value(),
                        ResponseMessage.CREATED_AMENITY,
                        amenityService.add(bannerPhotos, detailPhotos, amenityDTO)),
                HttpStatus.CREATED
        );
    }
}
