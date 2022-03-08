package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.dto.querydsl.AmenitySimpleDTO;
import com.monkeypenthouse.core.dto.querydsl.TicketOfAmenityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AmenityRepositoryCustom {
    Page<AmenitySimpleDTO> findAllByRecommended(int recommended, Pageable pageable);

    Page<AmenitySimpleDTO> findAll(Pageable pageable);

    Page<AmenitySimpleDTO> findAllByCategory(Long categoryId, Pageable pageable);

    List<TicketOfAmenityDto> getTicketsOfAmenity(Long amenityId);
}
