package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.dto.AmenityDTO.*;
import com.monkeypenthouse.core.dto.querydsl.TicketOfAmenityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AmenityRepositoryCustom {
    Page<ListDTO> findAllByRecommended(int recommended, Pageable pageable);

    List<TicketOfAmenityDto> getTicketsOfAmenity(Long amenityId);
}
