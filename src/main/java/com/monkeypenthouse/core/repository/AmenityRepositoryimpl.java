package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.dto.AmenityDTO;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AmenityRepositoryimpl implements AmenityRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AmenityDTO.ListDTO> findAllByRecommended(int recommended, Pageable pageable) {
        return null;
    }
}
