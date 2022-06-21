package com.monkeypenthouse.core.component;

import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.repository.entity.Purchase;
import com.monkeypenthouse.core.exception.CommonException;
import com.monkeypenthouse.core.repository.*;
import com.monkeypenthouse.core.repository.dto.PurchaseTicketMappingDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.redisson.api.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    public Long getTotalQuantityOfTicket(Long ticketId) {
        Long value =  (Long) redissonClient.getBucket(ticketId + ":totalQuantity").get();
        return value != null ? value : ticketStockRepository.findByTicketId(ticketId).getTotalQuantity();
    }

    public void setTotalQuantityOfTicket(Long ticketId, Long totalQuantity) {
        redissonClient.getBucket(ticketId + ":totalQuantity").set(totalQuantity);
    }

    public Long getPurchasedQuantityOfTicket(Long ticketId) {
        Long value = (Long) redissonClient.getBucket(ticketId + ":purchasedQuantity").get();
        return value != null ? value : ticketStockRepository.findByTicketId(ticketId).getPurchasedQuantity();
    }

    public void setPurchasedQuantityOfTicket(Long ticketId, Long purchasedQuantity) {
        redissonClient.getBucket(ticketId + ":purchasedQuantity").set(purchasedQuantity);
    }

    public Long addPurchasedQuantityOfTicket(Long ticketId, Long amount) {
        return redissonClient.getAtomicLong(ticketId + ":purchasedQuantity").addAndGet(amount);
    }


    public Map<Long, Long> getTicketInfoOfPurchase(String orderId) {
        RMap<Long, Long> rMap = redissonClient.getMap(orderId + ":ticketInfo");
        if (rMap != null) return rMap.readAllMap();

        Map<Long, Long> map = new HashMap<>();
        purchaseTicketMappingRepository.findAllByOrderId(orderId).forEach(
                pt -> map.put(pt.getTicket().getId(), pt.getQuantity())
        );
        return map;
    }

    public void setTicketInfoOfPurchase(String orderId, List<PurchaseTicketMappingDto> list) {
        RMap<Long, Long> rMap = redissonClient.getMap(orderId + ":ticketInfo");
        list.forEach(e -> rMap.put(e.getTicketId(), e.getQuantity()));
    }

    public void removeTicketInfoOfPurchase(String orderId) {
        redissonClient.getMap(orderId + ":ticketInfo").delete();
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

    public Long getTotalQuantityOfAmenity(Long amenityId) {
        Long value = (Long) redissonClient.getBucket(amenityId + ":totalQuantityOfTickets").get();
        return value != null ? value : amenityRepository.countTotalQuantity(amenityId);
    }

    public void setTotalQuantityOfAmenity(Long amenityId, Long totalQuantity) {
        redissonClient.getAtomicLong(amenityId + ":totalQuantityOfTickets").set(totalQuantity);
    }

    public long getPurchasedQuantityOfAmenity(Long amenityId) {
        Long value = (Long) redissonClient.getBucket(amenityId + ":purchasedQuantityOfTickets").get();
        return value != null ? value : amenityRepository.countTotalQuantity(amenityId);
    }

    public void setPurchasedQuantityOfAmenity(Long amenityId, long purchasedQuantity) {
        redissonClient.getAtomicLong(amenityId + ":purchasedQuantityOfTickets").set(purchasedQuantity);
    }

    public Long addPurchasedQuantityOfAmenity(Long amenityId, long amount) {
        return redissonClient.getAtomicLong(amenityId + ":purchasedQuantityOfTickets").addAndGet(amount);
    }

    // 현재 사용해야할 SerialNum을 반환
    // => 날짜가 바뀌거나, 전에 아무런 이력이 없었을 경우 : 1L 반환
    // => 나머지 경우 : 마지막 이력 + 1L 값 반환
    public Long getOrderIdSerialNum() {
        Long value = (Long) redissonClient.getBucket("orderIdSerialNum").get();
        if (value != null) {
            return value + 1;
        } else {
            Optional<Purchase> optionalPurchase = purchaseRepository.findTop1ByOrderByIdDesc();
            if (optionalPurchase.isPresent()) {
                LocalDate lastDate = LocalDate.parse(
                        optionalPurchase.get().getOrderId().substring(0, 6), DateTimeFormatter.ofPattern("yyMMdd")
                );
                if (lastDate.isBefore(LocalDate.now())) {
                    return Long.parseLong(optionalPurchase.get().getOrderId().substring(6)) + 1L;
                } else {
                    return 1L;
                }
            } else {
                return 1L;
            }
        }
    }

    public Optional<Long> getOrderIdSerialNumFromCache() {
        return Optional.ofNullable((Long) redissonClient.getBucket("orderIdSerialNum").get());
    }

    public void setOrderIdSerialNum(Long serialNum) {
        redissonClient.getBucket("orderIdSerialNum").set(serialNum);
    }

    public LockWithTimeOut tryLockOnOrderIdSerialNum(int waitSeconds, int leaseSeconds) {
        return tryLock("orderIdSerialNum", waitSeconds, leaseSeconds, TimeUnit.SECONDS);
    }

    public LockWithTimeOut tryMultiLockOnPurchasedQuantityOfTicket(List<Long> ticketIds) {
        return tryMultiLock(ticketIds.stream().map(e -> e + ":purchasedQuantity").collect(Collectors.toList()),
                2, 2, TimeUnit.SECONDS);
    }

    public LockWithTimeOut tryLock(String key, int waitSeconds, int leaseSeconds, TimeUnit timeUnit) {
        try {
            RLock lock = redissonClient.getLock(key + ":lock");
            if (!lock.tryLock(waitSeconds, leaseSeconds, timeUnit)) {
                throw new CommonException(ResponseCode.LOCK_FAILED);
            }
            return new LockWithTimeOut(lock, System.currentTimeMillis() + leaseSeconds * 1000L);
        } catch (InterruptedException e) {
            throw new CommonException(ResponseCode.LOCK_FAILED);
        }
    }

    public void unlock(RLock lock) {
        try { lock.unlock(); } catch (Exception ignored) {}
    }

    public LockWithTimeOut tryMultiLock(List<String> keys, int waitSeconds, int leaseSeconds, TimeUnit timeUnit) {
        try {
            RLock multiLock = redissonClient.getMultiLock(
                    keys.stream().map(
                            key -> redissonClient.getLock(key + ":lock")
                    ).collect(Collectors.toList()).toArray(RLock[]::new));
            if (!multiLock.tryLock(waitSeconds, leaseSeconds, timeUnit)) {
                throw new CommonException(ResponseCode.LOCK_FAILED);
            }
            return new LockWithTimeOut(multiLock, System.currentTimeMillis() + leaseSeconds * 1000L);
        } catch (InterruptedException e) {
            throw new CommonException(ResponseCode.LOCK_FAILED);
        }
    }

    @Getter
    public static class LockWithTimeOut {
        private final RLock rLock;
        private final long timeOut;

        public LockWithTimeOut(RLock rLock, long timeOut) {
            this.rLock = rLock;
            this.timeOut = timeOut;
        }
    }
}
