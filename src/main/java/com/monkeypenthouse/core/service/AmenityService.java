package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.dto.AmenityDTO.*;

public interface AmenityService {
    AmenityDetailDTO getById(Long id) throws Exception;
}
