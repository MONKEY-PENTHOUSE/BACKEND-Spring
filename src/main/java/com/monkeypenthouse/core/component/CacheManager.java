package com.monkeypenthouse.core.component;

import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.entity.Amenity;
import com.monkeypenthouse.core.entity.Purchase;
import com.monkeypenthouse.core.entity.TicketStock;
import com.monkeypenthouse.core.exception.CommonException;
import com.monkeypenthouse.core.repository.*;
import com.monkeypenthouse.core.vo.PurchaseTicketMappingVo;
import lombok.RequiredArgsConstructor;
import org.redisson.api.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CacheManager {

    private final RedissonClient redissonClient;
    private final TicketRepository ticketRepository;
    private final PurchaseTicketMappingRepository purchaseTicketMappingRepository;

    private final PurchaseRepository purchaseRepository;
    private final AmenityRepository amenityRepository;
    private final TicketStockRepository ticketStockRepository;

    public int getTotalQuantityOfTicket(Long ticketId) {
        Integer value = (Integer) redissonClient.getBucket(ticketId + ":totalQuantity").get();
        return value != null ? value : ticketStockRepository.findByTicketId(ticketId).getTotalQuantity();
    }

    public void setTotalQuantityOfTicket(Long ticketId, int totalQuantity) {
        redissonClient.getBucket(ticketId + ":totalQuantity").set(totalQuantity);
    }

    public int getPurchasedQuantityOfTicket(Long ticketId) {
        Integer value = (Integer) redissonClient.getBucket(ticketId + ":purchasedQuantity").get();
        return value != null ? value : ticketStockRepository.findByTicketId(ticketId).getPurchasedQuantity();
    }

    public void setPurchasedQuantityOfTicket(Long ticketId, int purchasedQuantity) {
        redissonClient.getBucket(ticketId + ":purchasedQuantity").set(purchasedQuantity);
    }

    public Map<Long, Integer> getTicketInfoOfPurchase(String orderId) {
        RMap<Long, Integer> rMap = redissonClient.getMap(orderId + ":ticketQuantity");
        if (rMap != null) return rMap.readAllMap();

        Map<Long, Integer> map = new HashMap<>();
        purchaseTicketMappingRepository.findAllByOrderId(orderId).forEach(
                pt -> map.put(pt.getTicket().getId(), pt.getQuantity())
        );
        return map;
    }

    public void setTicketInfoOfPurchase(String orderId, List<PurchaseTicketMappingVo> list) {
        RMap<Long, Integer> rMap = redissonClient.getMap(orderId + ":ticketQuantity");
        list.forEach(e -> rMap.put(e.getTicketId(), e.getQuantity()));
    }

    public Long getAmenityIdOfTicket(Long ticketId) {
        Long value = (Long) redissonClient.getBucket(ticketId + ":amenityId").get();
        return value != null ? value : ticketRepository.findById(ticketId)
                .orElseThrow(IllegalArgumentException::new)
                .getAmenity().getId();
    }

    public void setAmenityIdOfTicket(Long ticketId, Long amenityId) {
        redissonClient.getBucket(ticketId + ":amenityId").set(amenityId);
    }

    public int getTotalQuantityOfAmenity(Long amenityId) {
        Integer value = (Integer) redissonClient.getBucket(amenityId + ":totalQuantityOfTickets").get();
        return value != null ? value : amenityRepository.countTotalQuantity(amenityId);
    }

    public void setTotalQuantityOfAmenity(Long amenityId, int totalQuantity) {
        redissonClient.getBucket(amenityId + ":totalQuantityOfTickets").set(totalQuantity);
    }

    public int getPurchasedQuantityOfAmenity(Long amenityId) {
        Integer value = (Integer) redissonClient.getBucket(amenityId + ":purchasedQuantityOfTickets").get();
        return value != null ? value : amenityRepository.countTotalQuantity(amenityId);
    }

    public void setPurchasedQuantityOfAmenity(Long amenityId, int purchasedQuantity) {
        redissonClient.getBucket(amenityId + ":purchasedQuantityOfTickets").set(purchasedQuantity);
    }

    public Long getOrderIdSerialNum() {
        Long value = (Long) redissonClient.getBucket("orderIdSerialNum").get();
        if (value != null) {
            return value;
        } else {
            Optional<Purchase> optionalPurchase = purchaseRepository.findTop1ByOrderByIdDesc();
            return optionalPurchase.isEmpty() ? 1 :
                    Long.parseLong(optionalPurchase.get().getOrderId().substring(6));
        }
    }

    public void setOrderIdSerialNum(Long serialNum) {
        redissonClient.getBucket("orderIdSerialNum").set(serialNum);
    }

    @PostConstruct
    @Transactional(readOnly = true)
    private void loadPurchaseDataOnRedis() {

        final List<Amenity> amenityList = amenityRepository.findAllWithTicketsUsingFetchJoin();

        for (Amenity amenity : amenityList) {
            Set<Long> ticketIds = amenity.getTickets().stream().map(t -> t.getId()).collect(Collectors.toSet());
            List<TicketStock> ticketStocks = ticketStockRepository.findAllByTicketIdIn(ticketIds);

            setTotalQuantityOfAmenity(amenity.getId(),
                    ticketStocks.stream().mapToInt(TicketStock::getTotalQuantity).sum());
            setPurchasedQuantityOfAmenity(amenity.getId(),
                    ticketStocks.stream().mapToInt(TicketStock::getPurchasedQuantity).sum());

            for (TicketStock ticketStock : ticketStocks) {
                setTotalQuantityOfTicket(ticketStock.getTicketId(),
                        ticketStock.getTotalQuantity());
                setPurchasedQuantityOfTicket(ticketStock.getTicketId(),
                        ticketStock.getPurchasedQuantity());
            }
        }
    }

    public RLock tryLockOnOrderIdSerialNum(int waitSeconds, int leaseSeconds) {
        return tryLock("orderIdSerialNum", waitSeconds, leaseSeconds, TimeUnit.SECONDS);
    }

    public RLock tryMultiLockOnPurchasedQuantityOfTicket(List<Long> ticketIds) {
        return tryMultiLock(ticketIds.stream().map(e -> e + ":purchasedQuantity").collect(Collectors.toList()),
                2, 1, TimeUnit.SECONDS);
    }

    public RLock tryLock(String key, int waitSeconds, int leaseSeconds, TimeUnit timeUnit) {
        try {
            RLock lock = redissonClient.getLock(key);
            if (lock != null
                    && lock.tryLock(waitSeconds, leaseSeconds, timeUnit)) {
                throw new CommonException(ResponseCode.LOCK_FAILED);
            }
            return lock;
        } catch (InterruptedException e) {
            throw new CommonException(ResponseCode.LOCK_FAILED);
        }
    }

    public void unlock(RLock lock) {
        lock.unlock();
    }

    public RLock tryMultiLock(List<String> keys, int waitSeconds, int leaseSeconds, TimeUnit timeUnit) {
        try {
            RLock multiLock = redissonClient.getMultiLock(
                    keys.stream().map(
                            key -> redissonClient.getLock(key)
                    ).collect(Collectors.toList()).toArray(RLock[]::new));
            if (multiLock.tryLock(waitSeconds, leaseSeconds, timeUnit)) {
                throw new CommonException(ResponseCode.LOCK_FAILED);
            }
            return multiLock;
        } catch (InterruptedException e) {
            throw new CommonException(ResponseCode.LOCK_FAILED);
        }
    }
}
