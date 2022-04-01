package com.monkeypenthouse.core.controller;

import com.monkeypenthouse.core.component.CommonResponseMaker;
import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.dto.AddCarouselsRequestDTO;
import com.monkeypenthouse.core.dto.GetCarouselsResponseDTO;
import com.monkeypenthouse.core.service.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/photo")
@Log4j2
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;
    private final CommonResponseMaker commonResponseMaker;

    @PostMapping(value = "/carousel")
    public CommonResponseMaker.CommonResponseEntity addCarousels(@ModelAttribute AddCarouselsRequestDTO addReqDTO) throws Exception {
        photoService.addCarousels(addReqDTO);

        return commonResponseMaker.makeCommonResponse(ResponseCode.SUCCESS);
    }

    @GetMapping(value = "/carousel")
    public CommonResponseMaker.CommonResponseEntity getCarousels() throws Exception {
        GetCarouselsResponseDTO responseDto = GetCarouselsResponseDTO.of(photoService.getCarousels());
        return commonResponseMaker.makeCommonResponse(responseDto, ResponseCode.SUCCESS);
    }

}
