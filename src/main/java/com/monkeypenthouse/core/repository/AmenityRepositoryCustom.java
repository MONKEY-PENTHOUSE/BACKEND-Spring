package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.dto.AmenityDTO.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AmenityRepositoryCustom {
    Page<ListDTO> findAllByRecommended(int recommended, Pageable pageable);

    Page<ListDTO> findAll(Pageable pageable);

    Page<ListDTO> findAllByCategory(Long categoryId, Pageable pageable);
}
