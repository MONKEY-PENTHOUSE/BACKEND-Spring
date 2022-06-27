package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.repository.dto.PurchaseOfAmenityDto;
import com.monkeypenthouse.core.repository.dto.QTicketOfOrderedDto;
import com.monkeypenthouse.core.repository.dto.TicketOfOrderedDto;
import com.monkeypenthouse.core.repository.entity.OrderStatus;
import com.monkeypenthouse.core.repository.entity.Purchase;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static com.monkeypenthouse.core.repository.entity.QAmenity.amenity;
import static com.monkeypenthouse.core.repository.entity.QPurchase.purchase;
import static com.monkeypenthouse.core.repository.entity.QPurchaseTicketMapping.purchaseTicketMapping;
import static com.monkeypenthouse.core.repository.entity.QTicket.ticket;
import static com.monkeypenthouse.core.repository.entity.QUser.user;

@RequiredArgsConstructor
public class PurchaseRepositoryImpl implements PurchaseRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PurchaseOfAmenityDto> findAllByAmenityIdAndUserId(Long userId, Long amenityId) {
        List<Purchase> purchases = queryFactory
                .selectFrom(purchase)
                .where(
                        purchase.user.id.eq(userId),
                        purchase.amenityId.eq(amenityId),
                        purchase.orderStatus.eq(OrderStatus.COMPLETED)
                )
                .orderBy(purchase.createdAt.desc())
                .fetch();
        Map<Long, List<TicketOfOrderedDto>> aMap = queryFactory
                .from(purchaseTicketMapping)
                .innerJoin(purchaseTicketMapping.ticket, ticket)
                .where(purchaseTicketMapping.purchase.in(purchases))
                .groupBy(purchaseTicketMapping.purchase)
      .where(boardOneToOneJpaEntity.bbsEtc1.ne("1"))
                .orderBy(boardOneToOneJpaEntity.bbsDate.asc())
                .transform(
                        GroupBy.groupBy(boardOneToOneJpaEntity.groups).`as`(
                                GroupBy.list(new QTicketOfOrderedDto(

                        )
        ))
      )
        val qnaList = qList.map {
            BoardOneToOneQnAResultData(it, aMap.get(it.groups) ?: emptyList<BoardOneToOneAnswerD>())
        }

    }
}
