package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.dto.AddCarouselsRequestDTO;
import com.monkeypenthouse.core.vo.GetCarouselsResponseVo;
import org.jets3t.service.CloudFrontServiceException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

public interface PhotoService {
    void addCarousels(AddCarouselsRequestDTO addReqDTO) throws IOException;
    GetCarouselsResponseVo getCarousels() throws CloudFrontServiceException, IOException;

    void syncCarouselToRedis();
}
