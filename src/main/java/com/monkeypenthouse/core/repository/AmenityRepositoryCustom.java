package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.repository.dto.AmenitySimpleDTO;
import com.monkeypenthouse.core.repository.dto.CurrentPersonAndFundingPriceAndDibsOfAmenityDTO;
import com.monkeypenthouse.core.repository.dto.TicketOfAmenityDto;
import com.monkeypenthouse.core.repository.dto.TicketOfOrderedDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AmenityRepositoryCustom {

    Optional<CurrentPersonAndFundingPriceAndDibsOfAmenityDTO> findcurrentPersonAndFundingPriceAndDibsOfAmenityById(Long id);

    Page<AmenitySimpleDTO> findPageByRecommended(int recommended, Pageable pageable);

    Page<AmenitySimpleDTO> findPage(Pageable pageable);

    Page<AmenitySimpleDTO> findPageByCategory(Long categoryId, Pageable pageable);

    Page<AmenitySimpleDTO> findPageByDibsOfUser(Long userId, Pageable pageable);

    List<TicketOfAmenityDto> getTicketsOfAmenity(Long amenityId);

    List<AmenitySimpleDTO> findAllById(List<Long> amenityIds);

    Page<AmenitySimpleDTO> findPageByOrdered(Long id, Pageable pageable);

    int countTotalQuantity(Long amenityId);

    int countPurchasedQuantity(Long amenityId);

    List<TicketOfOrderedDto> getTicketsOfOrderedAmenity(Long userId, Long amenityId);
}
