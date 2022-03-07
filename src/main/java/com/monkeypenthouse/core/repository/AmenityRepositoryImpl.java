package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.dto.AmenityDTO.*;
import com.monkeypenthouse.core.entity.*;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AmenityRepositoryImpl extends QuerydslRepositorySupport implements AmenityRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public AmenityRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        super(Amenity.class);
        this.queryFactory = jpaQueryFactory;
    }

    @Override
    public Page<ListDTO> findAllByRecommended(int recommended, Pageable pageable) {
        QAmenity amenity = QAmenity.amenity;
        JPQLQuery<ListDTO> query = getQueryForListDTO().where(amenity.recommended.eq(recommended));
        long totalCount = queryFactory.selectFrom(amenity).where(amenity.recommended.eq(recommended)).fetchCount();
        List<ListDTO> results = getQuerydsl().applyPagination(pageable, query).fetch();
        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public Page<ListDTO> findAll(Pageable pageable) {
        QAmenity amenity = QAmenity.amenity;
        JPQLQuery<ListDTO> query = getQueryForListDTO();
        long totalCount = queryFactory.selectFrom(amenity).fetchCount();
        List<ListDTO> results = getQuerydsl().applyPagination(pageable, query).fetch();
        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public Page<ListDTO> findAllByCategory(Long categoryId, Pageable pageable) {
        QAmenity amenity = QAmenity.amenity;
        QAmenityCategory AmenityCategory = QAmenityCategory.amenityCategory;
        JPQLQuery<ListDTO> query = getQueryForListDTO();
        query.leftJoin(amenity.categories, AmenityCategory)
                .where(AmenityCategory.category.id.eq(categoryId));
        long totalCount = queryFactory.selectFrom(amenity)
                .leftJoin(amenity.categories, AmenityCategory)
                .where(AmenityCategory.category.id.eq(categoryId))
                .fetchCount();
        List<ListDTO> results = getQuerydsl().applyPagination(pageable, query).fetch();
        return new PageImpl<>(results, pageable, totalCount);
    }

    private JPQLQuery<ListDTO> getQueryForListDTO() {
        QAmenity amenity = QAmenity.amenity;
        QTicket ticket = QTicket.ticket;
        QParticipateIn participateIn = QParticipateIn.participateIn;

        return queryFactory.from(amenity)
                .leftJoin(amenity.tickets, ticket)
                .leftJoin(ticket.participateIns, participateIn)
                .groupBy(amenity.id)
                .select(
                        Projections.fields(
                                ListDTO.class,
                                amenity.id,
                                amenity.title,
                                amenity.minPersonNum.as("minPerson"),
                                amenity.maxPersonNum.as("maxPerson"),
                                amenity.thumbnailName,
                                participateIn.count.sum().as("currentPerson")
                        )

                );
    }


}
