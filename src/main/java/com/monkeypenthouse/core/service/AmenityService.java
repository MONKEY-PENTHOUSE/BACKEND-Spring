package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.entity.Amenity;
import com.monkeypenthouse.core.dto.AmenityDTO.*;
import com.monkeypenthouse.core.exception.DataNotFoundException;
import com.monkeypenthouse.core.vo.*;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AmenityService {
    void add(List<MultipartFile> bannerPhotos, List<MultipartFile> detailPhotos, SaveReqDTO amenityDTO) throws Exception;
    GetByIdResponseVo getById(Long id) throws Exception;

    List<Amenity> getAmenitiesDibsOn(UserDetails userDetails) throws DataNotFoundException;

    GetPageResponseVo getPageByRecommended(Pageable pageable) throws Exception;

    GetPageResponseVo getPage(Pageable pageable) throws Exception;

    GetPageResponseVo getPageByCategory(Long category, Pageable pageable) throws Exception;
    GetTicketsOfAmenityResponseVo getTicketsOfAmenity(Long amenityId);
}
