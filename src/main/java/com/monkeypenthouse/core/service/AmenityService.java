package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.dto.AmenityDTO.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AmenityService {
    SaveReqDTO add(List<MultipartFile> bannerPhotos, List<MultipartFile> detailPhotos, SaveReqDTO amenityDTO) throws Exception;
    DetailDTO getById(Long id) throws Exception;
}
