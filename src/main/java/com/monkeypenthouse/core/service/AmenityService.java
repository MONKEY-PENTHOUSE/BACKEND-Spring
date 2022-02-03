package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.dto.AmenityDTO.*;

public interface AmenityService {
    SaveReqDTO add(SaveReqDTO amenityDTO) throws Exception;
    DetailDTO getById(Long id) throws Exception;
}
