package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.dto.querydsl.AmenitySimpleDTO;
import com.monkeypenthouse.core.dto.querydsl.QAmenitySimpleDTO;
import com.monkeypenthouse.core.dto.querydsl.QTicketOfAmenityDto;
import com.monkeypenthouse.core.dto.querydsl.TicketOfAmenityDto;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.List;

import static com.monkeypenthouse.core.entity.QAmenity.*;
import static com.monkeypenthouse.core.entity.QParticipateIn.*;
import static com.monkeypenthouse.core.entity.QTicket.*;
import static com.monkeypenthouse.core.entity.QAmenityCategory.*;
import static com.monkeypenthouse.core.entity.QCategory.*;

@RequiredArgsConstructor
public class AmenityRepositoryImpl implements AmenityRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AmenitySimpleDTO> findAllByRecommended(int recommended, Pageable pageable) {
        List<AmenitySimpleDTO> content = getQueryForListDTO()
                .where(amenity.recommended.eq(recommended))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long totalCount = queryFactory.selectFrom(amenity).where(amenity.recommended.eq(recommended)).fetchCount();
        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public Page<AmenitySimpleDTO> findAll(Pageable pageable) {
        List<AmenitySimpleDTO> content = getQueryForListDTO()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long totalCount = queryFactory.selectFrom(amenity).fetchCount();
        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public Page<AmenitySimpleDTO> findAllByCategory(Long categoryId, Pageable pageable) {
        List<AmenitySimpleDTO> content = getQueryForListDTO()
                .leftJoin(amenity.categories, amenityCategory)
                .leftJoin(amenityCategory.category, category)
                .where(category.id.eq(categoryId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long totalCount = queryFactory.selectFrom(amenity)
                .leftJoin(amenity.categories, amenityCategory)
                .leftJoin(amenityCategory.category, category)
                .where(category.id.eq(categoryId))
                .fetchCount();
        return new PageImpl<>(content, pageable, totalCount);
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

    private JPQLQuery<AmenitySimpleDTO> getQueryForListDTO() {
        return queryFactory.from(amenity)
                .leftJoin(amenity.tickets, ticket)
                .leftJoin(ticket.participateIns, participateIn)
                .groupBy(amenity.id)
                .select(new QAmenitySimpleDTO(
                        amenity.id,
                        amenity.title,
                        amenity.minPersonNum.as("minPerson"),
                        amenity.maxPersonNum.as("maxPerson"),
                        participateIn.count.sum().coalesce(0).as("currentPerson"),
                        amenity.thumbnailName
                )

        );
    }
}
