package com.monkeypenthouse.core.repository;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.monkeypenthouse.core.dto.querydsl.*;
import com.monkeypenthouse.core.entity.Photo;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.monkeypenthouse.core.entity.QAmenity.*;
import static com.monkeypenthouse.core.entity.QParticipateIn.*;
import static com.monkeypenthouse.core.entity.QTicket.*;
import static com.monkeypenthouse.core.entity.QAmenityCategory.*;
import static com.monkeypenthouse.core.entity.QCategory.*;
import static com.monkeypenthouse.core.entity.QPhoto.*;
import static com.monkeypenthouse.core.entity.QDibs.*;
import static com.querydsl.core.group.GroupBy.*;


@RequiredArgsConstructor
public class AmenityRepositoryImpl implements AmenityRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Optional<AmenityDetailDTO> findDetailById(Long id) {
        Map<Long, AmenityDetailDTO> list = queryFactory
                .from(amenity)
                .leftJoin(amenity.categories, amenityCategory)
                .leftJoin(amenityCategory.category, category)
                .leftJoin(amenity.photos, photo)
                .leftJoin(amenity.tickets, ticket)
                .leftJoin(ticket.participateIns, participateIn)
                .leftJoin(amenity.dibs, dibs)
                .where(amenity.id.eq(id))
                .transform(
                        groupBy(amenity.id).as(
                                new QAmenityDetailDTO(
                                        amenity.id,
                                        amenity.title,
                                        amenity.detail,
                                        amenity.address,
                                        amenity.startDate,
                                        amenity.deadlineDate,
                                        list(new QPhotoDTO(
                                                photo.name,
                                                photo.type,
                                                photo.createdAt
                                        )),
                                        list(category.name),
                                        amenity.recommended,
                                        amenity.minPersonNum,
                                        amenity.maxPersonNum,
                                        participateIn.count.sum().coalesce(0),
                                        amenity.status,
                                        ticket.price.multiply(participateIn.count.coalesce(0)).sum(),
                                        dibs.count().coalesce(0L)
                                )
                        )
                );
        return Optional.ofNullable(list.get(id));
    }


    @Override
    public Page<AmenitySimpleDTO> findPageByRecommended(int recommended, Pageable pageable) {
        List<AmenitySimpleDTO> content = getQueryForListDTO()
                .where(amenity.recommended.eq(recommended))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long totalCount = queryFactory.selectFrom(amenity).where(amenity.recommended.eq(recommended)).fetchCount();
        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public Page<AmenitySimpleDTO> findPage(Pageable pageable) {
        List<AmenitySimpleDTO> content = getQueryForListDTO()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long totalCount = queryFactory.selectFrom(amenity).fetchCount();
        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public Page<AmenitySimpleDTO> findPageByCategory(Long categoryId, Pageable pageable) {
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
                        amenity.minPersonNum,
                        amenity.maxPersonNum,
                        participateIn.count.sum().coalesce(0).as("currentPersonNum"),
                        amenity.thumbnailName,
                        amenity.address,
                        amenity.startDate,
                        amenity.status
                )
        );
    }
}
