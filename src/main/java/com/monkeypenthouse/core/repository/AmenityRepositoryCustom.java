package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.dto.querydsl.AmenityDetailDTO;
import com.monkeypenthouse.core.dto.querydsl.AmenitySimpleDTO;
import com.monkeypenthouse.core.dto.querydsl.TicketOfAmenityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AmenityRepositoryCustom {

    Optional<AmenityDetailDTO> findDetailById(Long id);

    Page<AmenitySimpleDTO> findPageByRecommended(int recommended, Pageable pageable);

    Page<AmenitySimpleDTO> findPage(Pageable pageable);

    Page<AmenitySimpleDTO> findPageByCategory(Long categoryId, Pageable pageable);

    List<TicketOfAmenityDto> getTicketsOfAmenity(Long amenityId);
}
