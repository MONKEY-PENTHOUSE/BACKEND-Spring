package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.dao.Amenity;
import com.monkeypenthouse.core.dto.AmenityDTO;
import com.monkeypenthouse.core.dto.AmenityDTO.*;
import com.monkeypenthouse.core.exception.DataNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AmenityService {
    void add(List<MultipartFile> bannerPhotos, List<MultipartFile> detailPhotos, SaveReqDTO amenityDTO) throws Exception;
    DetailDTO getById(Long id) throws Exception;

    List<Amenity> getAmenitiesDibsOn(UserDetails userDetails) throws DataNotFoundException;
}
