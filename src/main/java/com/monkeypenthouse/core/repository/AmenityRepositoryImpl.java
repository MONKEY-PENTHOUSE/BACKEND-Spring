package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.dto.AmenityDTO;
import com.monkeypenthouse.core.dto.querydsl.QTicketOfAmenityDto;
import com.monkeypenthouse.core.dto.querydsl.TicketOfAmenityDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

import static com.monkeypenthouse.core.entity.QAmenity.*;
import static com.monkeypenthouse.core.entity.QParticipateIn.*;
import static com.monkeypenthouse.core.entity.QTicket.*;

@RequiredArgsConstructor
public class AmenityRepositoryImpl implements AmenityRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AmenityDTO.ListDTO> findAllByRecommended(int recommended, Pageable pageable) {
        return null;
    }

    @Override
    public List<TicketOfAmenityDto> getTicketsOfAmenity(Long amenityId) {
        return queryFactory
                .select(new QTicketOfAmenityDto(
                        ticket.id,
                        ticket.name,
                        ticket.detail,
                        ticket.capacity,
                        ticket.price,
                        participateIn.count.sum().coalesce(0)
                ))
                .from(ticket)
                .where(ticket.amenity.id.eq(amenityId))
                .rightJoin(ticket.amenity, amenity)
                .leftJoin(participateIn).on(ticket.id.eq(participateIn.ticket.id))
                .groupBy(ticket.id)
                .fetch();
    }
}
