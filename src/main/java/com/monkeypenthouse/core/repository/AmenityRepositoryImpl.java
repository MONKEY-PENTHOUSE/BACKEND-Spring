package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.dto.AmenityDTO.*;
import com.monkeypenthouse.core.entity.Amenity;
import com.monkeypenthouse.core.entity.QAmenity;
import com.monkeypenthouse.core.entity.QParticipateIn;
import com.monkeypenthouse.core.entity.QTicket;
import com.querydsl.core.types.Projections;
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
        QTicket ticket = QTicket.ticket;
        QParticipateIn participateIn = QParticipateIn.participateIn;

        JPQLQuery<ListDTO> query = queryFactory.from(amenity)
                .leftJoin(amenity.tickets, ticket)
                .leftJoin(ticket.participateIns, participateIn)
                .where(amenity.recommended.eq(1))
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
        long totalCount = queryFactory.selectFrom(amenity).where(amenity.recommended.eq(1)).fetchCount();
        List<ListDTO> results = getQuerydsl().applyPagination(pageable, query).fetch();
        return new PageImpl<>(results, pageable, totalCount);
    }


}
